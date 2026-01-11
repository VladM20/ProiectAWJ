package com.upb.ProiectAWJ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Nu trebuie sa scrii nimic aici, avem totul automat
}