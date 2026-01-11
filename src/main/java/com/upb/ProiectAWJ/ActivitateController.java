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

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listaActivitati", activitateRepository.findAll());
        return "index";
    }

    @GetMapping("/formularActivitate")
    public String showNewActivityForm(Model model) {
        Activitate activitate = new Activitate();
        model.addAttribute("activitate", activitate);
        return "form_activitate";
    }

    @PostMapping("/salveazaActivitate")
    public String saveActivity(@Valid @ModelAttribute("activitate") Activitate activitate, BindingResult result) {
        if (result.hasErrors()) {
            return "form_activitate";
        }

        // Logica simpla: Daca e finalizata, punem progres 100% automat
        if ("FINALIZATA".equals(activitate.getStare())) {
            activitate.setProgres(100);
        }

        activitateRepository.save(activitate);
        return "redirect:/";
    }

    @GetMapping("/editeazaActivitate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Activitate activitate = activitateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID:" + id));
        model.addAttribute("activitate", activitate);
        return "form_activitate";
    }

    @GetMapping("/stergeActivitate/{id}")
    public String deleteActivity(@PathVariable(value = "id") Long id) {
        this.activitateRepository.deleteById(id);
        return "redirect:/";
    }
}