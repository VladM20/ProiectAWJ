/** Clasa pentru Controller, contine metodele folosite in aplicatie
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Comparator;
import java.util.List;

@Controller
public class ActivitateController {

    @Autowired
    private ActivitateRepository activitateRepository;
    @Autowired
    private IndicatorRepository indicatorRepository;

    // Pagina Principala
    @GetMapping("/")
    public String viewHomePage(Model model, @RequestParam(value = "sort", required = false) String sort) {

        List<Activitate> lista = activitateRepository.findAll();

        // Logica de sortare
        if ("crescator".equals(sort)) {
            lista.sort(Comparator.comparingInt(Activitate::getProgres));
        } else if ("descrescator".equals(sort)) {
            lista.sort((a, b) -> b.getProgres() - a.getProgres());
        } else {
            // Default: inversate dupa ID (ultimele create sus)
            lista.sort((a, b) -> b.getId().compareTo(a.getId()));
        }

        model.addAttribute("listaActivitati", lista);
        model.addAttribute("sortareCurenta", sort);
        return "index";
    }

    // Adaugare Activitate
    @PostMapping("/adaugaActivitateRapida")
    public String adaugaActivitateRapida(@RequestParam("nume") String nume,
                                         @RequestParam("descriere") String descriere,
                                         RedirectAttributes redirectAttributes) {

        // Verificare duplicate
        if (activitateRepository.existsByNumeIgnoreCase(nume.trim())) {
            redirectAttributes.addFlashAttribute("eroare", "O activitate cu numele '" + nume.trim() + "' există deja!");
            return "redirect:/";
        }

        if (!nume.trim().isEmpty()) {
            Activitate act = new Activitate();
            act.setNume(nume.trim());
            act.setDescriere(descriere);
            activitateRepository.save(act);
        }
        return "redirect:/";
    }

    // Redenumire Activitate
    @PostMapping("/redenumesteActivitate")
    public String redenumesteActivitate(@RequestParam("id") Long id,
                                        @RequestParam("nume") String numeNou,
                                        RedirectAttributes redirectAttributes) {

        Activitate actCurenta = activitateRepository.findById(id).orElse(null);

        if (actCurenta != null && numeNou != null && !numeNou.trim().isEmpty()) {
            // Daca numele e diferit de cel vechi si deja exista in baza de date
            if (!actCurenta.getNume().equalsIgnoreCase(numeNou.trim()) &&
                    activitateRepository.existsByNumeIgnoreCase(numeNou.trim())) {

                redirectAttributes.addFlashAttribute("eroare", "Nu poți redenumi: Numele '" + numeNou.trim() + "' este deja folosit!");
                return "redirect:/";
            }

            actCurenta.setNume(numeNou.trim());
            activitateRepository.save(actCurenta);
        }
        return "redirect:/";
    }

    // Editare Descriere
    @PostMapping("/editeazaDescriere")
    public String editeazaDescriere(@RequestParam("id") Long id,
                                    @RequestParam("descriere") String descriereNoua) {
        Activitate act = activitateRepository.findById(id).orElse(null);
        if (act != null) {
            act.setDescriere(descriereNoua);
            activitateRepository.save(act);
        }
        return "redirect:/";
    }

    // Adaugare Indicator
    @PostMapping("/adaugaIndicatorRapid")
    public String adaugaIndicatorRapid(@RequestParam("activitateId") Long activitateId,
                                       @RequestParam("nume") String nume,
                                       RedirectAttributes redirectAttributes) {

        Activitate act = activitateRepository.findById(activitateId).orElse(null);

        if (act != null && nume != null && !nume.trim().isEmpty()) {

            // Verifica daca exista acest indicator deja in aceasta activitate
            if (indicatorRepository.existsByNumeIgnoreCaseAndActivitate(nume.trim(), act)) {
                redirectAttributes.addFlashAttribute("eroare", "Indicatorul '" + nume.trim() + "' există deja în această activitate!");
                return "redirect:/";
            }

            Indicator ind = new Indicator();
            ind.setNume(nume.trim());
            ind.setActivitate(act);
            indicatorRepository.save(ind);

            updateActivityProgress(act.getId());
        }
        return "redirect:/";
    }

    // Stergere Activitate
    @GetMapping("/stergeActivitate/{id}")
    public String deleteActivity(@PathVariable(value = "id") Long id) {
        this.activitateRepository.deleteById(id);
        return "redirect:/";
    }

    // Stergere Indicator
    @GetMapping("/stergeIndicator/{id}")
    public String deleteIndicator(@PathVariable(value = "id") Long id) {
        Indicator ind = indicatorRepository.findById(id).orElseThrow();
        Long parentId = ind.getActivitate().getId();
        indicatorRepository.delete(ind);
        updateActivityProgress(parentId);
        return "redirect:/";
    }

    // Toggle Indicator realizat sau nu
    @GetMapping("/toggleIndicator/{id}")
    public String toggleIndicator(@PathVariable Long id) {
        Indicator ind = indicatorRepository.findById(id).orElseThrow();
        ind.setRealizat(!ind.isRealizat());
        indicatorRepository.save(ind);
        updateActivityProgress(ind.getActivitate().getId());
        return "redirect:/";
    }

    // Toggle Activitate fara indicatori
    @GetMapping("/toggleActivitate/{id}")
    public String toggleActivitateSimpla(@PathVariable Long id) {
        Activitate act = activitateRepository.findById(id).orElseThrow();
        if (act.getIndicatori().isEmpty()) {
            if ("FINALIZATA".equals(act.getStare())) {
                act.setStare("PLANIFICATA");
                act.setProgres(0);
            } else {
                act.setStare("FINALIZATA");
                act.setProgres(100);
            }
            activitateRepository.save(act);
        }
        return "redirect:/";
    }

    // Calculul Procentului de Progres
    private void updateActivityProgress(Long activitateId) {
        Activitate act = activitateRepository.findById(activitateId).orElseThrow();
        List<Indicator> lista = act.getIndicatori();

        if (lista.isEmpty()) { return; }

        double total = lista.size();
        double rezolvate = 0;
        for (Indicator ind : lista) {
            if (ind.isRealizat()) rezolvate++;
        }

        int procentaj = (int) ((rezolvate / total) * 100);
        act.setProgres(procentaj);

        if (procentaj == 100) act.setStare("FINALIZATA");
        else if (procentaj > 0) act.setStare("IN_DESFASURARE");
        else act.setStare("PLANIFICATA");

        activitateRepository.save(act);
    }
}