/** Clasa pentru gestionarea interactiunii cu baza de date
 * @author Moișanu Cristian-Vlad
 * @version 11 Ianuarie 2026
 */

package com.upb.ProiectAWJ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivitateRepository extends JpaRepository<Activitate, Long> {
    // Putem adauga cautari personalizate aici daca e nevoie pe viitor
}