/** Clasa pentru Controller, intermediaza View si Model
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ActivitateController {

    @Autowired
    private ActivitateRepository activitateRepository;
    @Autowired
    private IndicatorRepository indicatorRepository;

    // --- MODIFICARE AICI: Adaugam parametrul 'sort' ---
    @GetMapping("/")
    public String viewHomePage(Model model, @RequestParam(value = "sort", required = false) String sort) {

        List<Activitate> lista = activitateRepository.findAll();

        // Logica de sortare
        if ("crescator".equals(sort)) {
            // Sorteaza de la 0% la 100%
            lista.sort(Comparator.comparingInt(Activitate::getProgres));
        } else if ("descrescator".equals(sort)) {
            // Sorteaza de la 100% la 0%
            lista.sort((a, b) -> b.getProgres() - a.getProgres());
        } else {
            // Default: Le afisam dupa ID (ordinea crearii), inversate ca sa vedem ultimele sus
            lista.sort((a, b) -> b.getId().compareTo(a.getId()));
        }

        model.addAttribute("listaActivitati", lista);
        model.addAttribute("sortareCurenta", sort); // Trimitem inapoi ca sa stim ce buton e activ
        return "index";
    }

    // ... RESTUL METODELOR RAMAN NESCHIMBATE (adaugaActivitateRapida, toggle, etc.) ...
    // Asigura-te ca pastrezi metodele 'adaugaActivitateRapida', 'redenumesteActivitate',
    // 'editeazaDescriere', 'adaugaIndicatorRapid', 'sterge...', 'toggle...' scrise anterior.

    // --- COPIAZA AICI RESTUL METODELOR DIN RASPUNSUL ANTERIOR ---

    @PostMapping("/adaugaActivitateRapida")
    public String adaugaActivitateRapida(@RequestParam("nume") String nume,
                                         @RequestParam("descriere") String descriere) {
        if (nume != null && !nume.trim().isEmpty()) {
            Activitate act = new Activitate();
            act.setNume(nume);
            act.setDescriere(descriere);
            activitateRepository.save(act);
        }
        return "redirect:/";
    }

    @PostMapping("/redenumesteActivitate")
    public String redenumesteActivitate(@RequestParam("id") Long id, @RequestParam("nume") String numeNou) {
        Activitate act = activitateRepository.findById(id).orElse(null);
        if (act != null && numeNou != null && !numeNou.trim().isEmpty()) {
            act.setNume(numeNou);
            activitateRepository.save(act);
        }
        return "redirect:/";
    }

    @PostMapping("/editeazaDescriere")
    public String editeazaDescriere(@RequestParam("id") Long id, @RequestParam("descriere") String descriereNoua) {
        Activitate act = activitateRepository.findById(id).orElse(null);
        if (act != null) {
            act.setDescriere(descriereNoua);
            activitateRepository.save(act);
        }
        return "redirect:/";
    }

    @PostMapping("/adaugaIndicatorRapid")
    public String adaugaIndicatorRapid(@RequestParam("activitateId") Long activitateId, @RequestParam("nume") String nume) {
        Activitate act = activitateRepository.findById(activitateId).orElse(null);
        if (act != null && nume != null && !nume.trim().isEmpty()) {
            Indicator ind = new Indicator();
            ind.setNume(nume);
            ind.setActivitate(act);
            indicatorRepository.save(ind);
            updateActivityProgress(act.getId());
        }
        return "redirect:/";
    }

    @GetMapping("/stergeActivitate/{id}")
    public String deleteActivity(@PathVariable(value = "id") Long id) {
        this.activitateRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/stergeIndicator/{id}")
    public String deleteIndicator(@PathVariable(value = "id") Long id) {
        Indicator ind = indicatorRepository.findById(id).orElseThrow();
        Long parentId = ind.getActivitate().getId();
        indicatorRepository.delete(ind);
        updateActivityProgress(parentId);
        return "redirect:/";
    }

    @GetMapping("/toggleIndicator/{id}")
    public String toggleIndicator(@PathVariable Long id) {
        Indicator ind = indicatorRepository.findById(id).orElseThrow();
        ind.setRealizat(!ind.isRealizat());
        indicatorRepository.save(ind);
        updateActivityProgress(ind.getActivitate().getId());
        return "redirect:/";
    }

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