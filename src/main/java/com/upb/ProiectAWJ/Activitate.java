package com.upb.ProiectAWJ;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Activitate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele activității este obligatoriu")
    private String nume;

    @Size(max = 500, message = "Descrierea e prea lungă")
    private String descriere;

    // Stari posibile: PLANIFICATA, IN_DESFASURARE, SUSPENDATA, FINALIZATA
    private String stare = "PLANIFICATA";

    // Progres (doar pentru cele in desfasurare, dar il tinem numeric 0-100)
    @Min(value = 0, message = "Progresul nu poate fi negativ")
    @Max(value = 100, message = "Progresul nu poate depăși 100%")
    private int progres = 0;

    // Indicator daca activitatea e repetitiva
    private boolean repetitiva = false;

    // --- GETTERS si SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public String getStare() { return stare; }
    public void setStare(String stare) { this.stare = stare; }

    public int getProgres() { return progres; }
    public void setProgres(int progres) { this.progres = progres; }

    public boolean isRepetitiva() { return repetitiva; }
    public void setRepetitiva(boolean repetitiva) { this.repetitiva = repetitiva; }
}