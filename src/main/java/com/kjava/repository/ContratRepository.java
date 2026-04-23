package com.kjava.repository;

import com.kjava.models.Contrat;
import com.kjava.models.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {
    Optional<Contrat> findByOperantIdAndStatutNot(Long operantId, Contrat.StatutContrat statut);
    Optional<Contrat> findByOperantAndStatutNot(Employe operant, Contrat.StatutContrat statut);
    List<Contrat> findByOperantId(Long operantId);
}