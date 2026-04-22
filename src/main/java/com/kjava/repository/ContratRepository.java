package com.kjava.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kjava.models.Contrat;


@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {
    Optional<Contrat> findByEmployeIdAndStatut(Long employeId, Contrat.StatutContrat statut);
}