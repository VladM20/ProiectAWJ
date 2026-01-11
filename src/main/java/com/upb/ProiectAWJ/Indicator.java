/** Clasa pentru definirea Indicatorilor
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

    private boolean realizat = false;

    // Relatia cu Activitate
    @ManyToOne
    @JoinColumn(name = "activitate_id", nullable = false)
    private Activitate activitate;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public boolean isRealizat() { return realizat; }
    public void setRealizat(boolean realizat) { this.realizat = realizat; }
    public Activitate getActivitate() { return activitate; }
    public void setActivitate(Activitate activitate) { this.activitate = activitate; }
}