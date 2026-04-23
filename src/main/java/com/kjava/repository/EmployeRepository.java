package com.kjava.repository;

import com.kjava.models.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    Optional<Employe> findByLogin(String login);
    List<Employe> findByRole(Employe.Role role);
}