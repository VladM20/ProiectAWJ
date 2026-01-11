package com.upb.ProiectAWJ;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele este obligatoriu!")
    @Size(min = 2, message = "Numele trebuie să aibă minim 2 caractere.")
    private String nume;

    @NotBlank(message = "Prenumele este obligatoriu!")
    private String prenume;

    @NotBlank(message = "Email-ul este obligatoriu!")
    @Email(message = "Formatul email-ului nu este valid (ex: a@b.com)")
    private String email;

    // Getters si Setters standard
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}