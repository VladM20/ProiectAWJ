/** Clasa pentru gestionarea tabelei Indicator din baza de date
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Long> {
    // Verifica daca exista un indicator cu acest nume in activitatea specificata
    boolean existsByNumeIgnoreCaseAndActivitate(String nume, Activitate activitate);
}