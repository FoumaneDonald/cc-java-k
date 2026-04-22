package com.kjava.repositories;

import com.kjava.entities.ParametrePaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParametrePaieRepository extends JpaRepository<ParametrePaie, UUID> {
    
    Optional<ParametrePaie> findByCodeAndDateEffetLessThanEqualOrderByDateEffetDescVersionDesc(
        String code, LocalDate dateEffet
    );
    
    List<ParametrePaie> findByCodeOrderByDateEffetDescVersionDesc(String code);
    
    @Query("SELECT p FROM ParametrePaie p WHERE p.code = :code AND p.dateEffet <= :dateEffet ORDER BY p.dateEffet DESC, p.version DESC")
    Optional<ParametrePaie> findLatestVersionByCodeAndDate(@Param("code") String code, @Param("dateEffet") LocalDate dateEffet);
    
    List<ParametrePaie> findByDateEffetBetweenOrderByDateEffetDesc(LocalDate startDate, LocalDate endDate);
    
    boolean existsByCodeAndDateEffetAndVersion(String code, LocalDate dateEffet, Integer version);
}
