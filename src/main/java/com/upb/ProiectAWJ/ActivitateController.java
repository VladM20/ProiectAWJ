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

    // --- LOGICA PENTRU ACTIVITATI ---

    @GetMapping("/formularActivitate")
    public String showNewActivityForm(Model model) {
        model.addAttribute("activitate", new Activitate());
        return "form_activitate";
    }

    @PostMapping("/salveazaActivitate")
    public String saveActivity(@Valid @ModelAttribute("activitate") Activitate activitate, BindingResult result) {
        if (result.hasErrors()) {
            return "form_activitate";
        }
        // La creare, progresul e 0. Calculul se face doar la modificarea indicatorilor.
        activitateRepository.save(activitate);
        return "redirect:/";
    }

    @GetMapping("/editeazaActivitate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Activitate activitate = activitateRepository.findById(id).orElse(null);
        model.addAttribute("activitate", activitate);
        return "form_activitate";
    }

    @GetMapping("/stergeActivitate/{id}")
    public String deleteActivity(@PathVariable(value = "id") Long id) {
        this.activitateRepository.deleteById(id);
        return "redirect:/";
    }

    // --- LOGICA PENTRU INDICATORI ---

    // Pagina pentru a adauga un indicator nou la o activitate
    @GetMapping("/adaugaIndicator/{idActivitate}")
    public String showAddIndicatorForm(@PathVariable Long idActivitate, Model model) {
        Activitate activitate = activitateRepository.findById(idActivitate).orElseThrow();
        Indicator indicator = new Indicator();
        indicator.setActivitate(activitate); // Legam indicatorul de parinte

        model.addAttribute("indicator", indicator);
        model.addAttribute("numeActivitate", activitate.getNume());
        return "form_indicator";
    }

    @PostMapping("/salveazaIndicator")
    public String saveIndicator(@ModelAttribute("indicator") Indicator indicator) {
        // Salvam indicatorul
        indicatorRepository.save(indicator);
        // Recalculam progresul parintelui
        updateActivityProgress(indicator.getActivitate().getId());
        return "redirect:/";
    }

    // --- LOGICA DINAMICA (CHECKBOXES) ---

    // 1. Toggle pentru o activitate SIMPLA (fara indicatori)
    @GetMapping("/toggleActivitate/{id}")
    public String toggleActivitateSimpla(@PathVariable Long id) {
        Activitate act = activitateRepository.findById(id).orElseThrow();

        // Daca e finalizata o trecem in planificata, si invers
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

    // 2. Toggle pentru un INDICATOR (Activitate complexa)
    @GetMapping("/toggleIndicator/{id}")
    public String toggleIndicator(@PathVariable Long id) {
        Indicator ind = indicatorRepository.findById(id).orElseThrow();

        // Schimbam starea checkbox-ului
        ind.setRealizat(!ind.isRealizat());
        indicatorRepository.save(ind);

        // Recalculam parintele
        updateActivityProgress(ind.getActivitate().getId());

        return "redirect:/";
    }

    // Metoda privata pentru recalculare automata
    private void updateActivityProgress(Long activitateId) {
        Activitate act = activitateRepository.findById(activitateId).orElseThrow();

        if (act.getIndicatori().isEmpty()) return;

        int totalCalculat = 0;
        for (Indicator ind : act.getIndicatori()) {
            if (ind.isRealizat()) {
                totalCalculat += ind.getPondere();
            }
        }

        // Cap la 100%
        if (totalCalculat > 100) totalCalculat = 100;
        act.setProgres(totalCalculat);

        // Actualizare stare automata
        if (totalCalculat == 100) {
            act.setStare("FINALIZATA");
        } else if (totalCalculat > 0) {
            act.setStare("IN_DESFASURARE");
        } else {
            act.setStare("PLANIFICATA");
        }

        activitateRepository.save(act);
    }
}