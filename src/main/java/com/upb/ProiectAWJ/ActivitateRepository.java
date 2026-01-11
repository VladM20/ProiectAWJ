/** Clasa pentru gestionarea tabelei Activitate din baza de date
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivitateRepository extends JpaRepository<Activitate, Long> {
    // Verifica daca exista o activitate cu acest nume (case insensitive)
    boolean existsByNumeIgnoreCase(String nume);
}