/** Clasa pentru Controller, intermediaza View si Model
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
public class ActivitateController {

    @Autowired
    private ActivitateRepository activitateRepository;
    @Autowired
    private IndicatorRepository indicatorRepository;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listaActivitati", activitateRepository.findAll());
        return "index";
    }

    // --- LOGICA ACTIVITATI ---
    @GetMapping("/formularActivitate")
    public String showNewActivityForm(Model model) {
        model.addAttribute("activitate", new Activitate());
        return "form_activitate";
    }

    @PostMapping("/salveazaActivitate")
    public String saveActivity(@Valid @ModelAttribute("activitate") Activitate activitate, BindingResult result) {
        if (result.hasErrors()) { return "form_activitate"; }
        activitateRepository.save(activitate);
        return "redirect:/";
    }

    @GetMapping("/editeazaActivitate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("activitate", activitateRepository.findById(id).orElse(null));
        return "form_activitate";
    }

    @GetMapping("/stergeActivitate/{id}")
    public String deleteActivity(@PathVariable(value = "id") Long id) {
        this.activitateRepository.deleteById(id);
        return "redirect:/";
    }

    // --- LOGICA INDICATORI ---
    @GetMapping("/adaugaIndicator/{idActivitate}")
    public String showAddIndicatorForm(@PathVariable Long idActivitate, Model model) {
        Activitate activitate = activitateRepository.findById(idActivitate).orElseThrow();
        Indicator indicator = new Indicator();
        indicator.setActivitate(activitate);

        model.addAttribute("indicator", indicator);
        model.addAttribute("numeActivitate", activitate.getNume());
        return "form_indicator";
    }

    @PostMapping("/salveazaIndicator")
    public String saveIndicator(@ModelAttribute("indicator") Indicator indicator) {
        indicatorRepository.save(indicator);
        // Recalculam progresul imediat ce adaugam un indicator nou (procentul scade daca adaugam cerinte noi)
        updateActivityProgress(indicator.getActivitate().getId());
        return "redirect:/";
    }

    // --- LOGICA DINAMICA ---

    @GetMapping("/toggleActivitate/{id}")
    public String toggleActivitateSimpla(@PathVariable Long id) {
        Activitate act = activitateRepository.findById(id).orElseThrow();
        if ("FINALIZATA".equals(act.getStare())) {
            act.setStare("PLANIFICATA");
            act.setProgres(0);
        } else {
            act.setStare("FINALIZATA");
            act.setProgres(100);
        }
        activitateRepository.save(act);
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

    // --- METODA NOUA DE CALCUL ---
    private void updateActivityProgress(Long activitateId) {
        Activitate act = activitateRepository.findById(activitateId).orElseThrow();
        List<Indicator> lista = act.getIndicatori();

        if (lista.isEmpty()) {
            // Daca nu are indicatori, progresul ramane cum a fost setat manual (0 sau 100)
            return;
        }

        double totalIndicatori = lista.size();
        double indicatoriRezolvati = 0;

        for (Indicator ind : lista) {
            if (ind.isRealizat()) {
                indicatoriRezolvati++;
            }
        }

        // Calcul procentaj: (Rezolvate / Total) * 100
        int procentajNou = (int) ((indicatoriRezolvati / totalIndicatori) * 100);
        act.setProgres(procentajNou);

        // Actualizare Stare Automata
        if (procentajNou == 100) {
            act.setStare("FINALIZATA");
        } else if (procentajNou > 0) {
            act.setStare("IN_DESFASURARE");
        } else {
            act.setStare("PLANIFICATA");
        }

        activitateRepository.save(act);
    }
}