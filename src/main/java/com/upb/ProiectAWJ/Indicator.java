/** Clasa pentru definirea Indicatorilor (Sub-task-uri).
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Indicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele indicatorului este obligatoriu")
    private String nume;

    @Min(1) @Max(100)
    private int pondere; // Cat la suta valoreaza acest indicator din totalul activitatii

    private boolean realizat = false;

    @ManyToOne
    @JoinColumn(name = "activitate_id", nullable = false)
    private Activitate activitate;

    // --- Getters, Setters, Constructor ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public int getPondere() { return pondere; }
    public void setPondere(int pondere) { this.pondere = pondere; }
    public boolean isRealizat() { return realizat; }
    public void setRealizat(boolean realizat) { this.realizat = realizat; }
    public Activitate getActivitate() { return activitate; }
    public void setActivitate(Activitate activitate) { this.activitate = activitate; }
}