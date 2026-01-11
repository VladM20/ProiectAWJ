package com.upb.ProiectAWJ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    // 1. VIZUALIZARE COLECTIE
    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listaStudenti", studentRepository.findAll());
        return "index";
    }

    // 2. FORMULAR ADAUGARE
    @GetMapping("/showNewStudentForm")
    public String showNewStudentForm(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "new_student";
    }

    // 3. SALVARE + VALIDARE + MODIFICARE
    @PostMapping("/saveStudent")
    public String saveStudent(@Valid @ModelAttribute("student") Student student, BindingResult result) {
        // Daca exista erori de validare (ex: nume gol), nu salvam, ci ramanem pe pagina
        if (result.hasErrors()) {
            return "new_student";
        }
        // Save face automat Update daca studentul are deja un ID setat
        studentRepository.save(student);
        return "redirect:/";
    }

    // 4. PREGATIRE MODIFICARE (Incarca datele existente in formular)
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student invalid ID:" + id));
        model.addAttribute("student", student);
        return "new_student";
    }

    // 5. STERGERE
    @GetMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable(value = "id") Long id) {
        this.studentRepository.deleteById(id);
        return "redirect:/";
    }
}