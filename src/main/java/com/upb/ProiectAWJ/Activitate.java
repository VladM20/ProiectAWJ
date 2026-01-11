/** Clasa pentru definirea Activitatilor
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Activitate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele este obligatoriu")
    private String nume;

    private String descriere;
    private String stare = "PLANIFICATA"; // PLANIFICATA, IN_DESFASURARE, FINALIZATA

    private int progres = 0;
    private boolean repetitiva = false;

    // Relatia cu Indicatorii
    @OneToMany(mappedBy = "activitate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Indicator> indicatori = new ArrayList<>();

    // Getters & Setters
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
    public List<Indicator> getIndicatori() { return indicatori; }
    public void setIndicatori(List<Indicator> indicatori) { this.indicatori = indicatori; }
}