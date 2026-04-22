package com.kjava.repositories;

import com.kjava.entities.FicheDePaie;
import com.kjava.enums.StatutBulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FicheDePaieRepository extends JpaRepository<FicheDePaie, UUID> {
    
    List<FicheDePaie> findByEmployeeId(UUID employeeId);
    
    List<FicheDePaie> findByLotBulletinPaieId(UUID lotBulletinPaieId);
    
    List<FicheDePaie> findByStatut(StatutBulletin statut);
    
    List<FicheDePaie> findByStructureSalarialeId(UUID structureSalarialeId);
    
    @Query("SELECT f FROM FicheDePaie f WHERE f.employeeId = :employeeId AND f.lotBulletinPaie.mois = :mois AND f.lotBulletinPaie.annee = :annee")
    Optional<FicheDePaie> findByEmployeeIdAndPeriod(@Param("employeeId") UUID employeeId, @Param("mois") Integer mois, @Param("annee") Integer annee);
    
    @Query("SELECT f FROM FicheDePaie f WHERE f.lotBulletinPaie.id = :lotId AND f.statut = :statut")
    List<FicheDePaie> findByLotIdAndStatut(@Param("lotId") UUID lotId, @Param("statut") StatutBulletin statut);
    
    @Query("SELECT COUNT(f) FROM FicheDePaie f WHERE f.lotBulletinPaie.id = :lotId AND f.statut = 'VALIDE'")
    long countValidatedByLotId(@Param("lotId") UUID lotId);
    
    @Query("SELECT SUM(f.netAPayer) FROM FicheDePaie f WHERE f.lotBulletinPaie.id = :lotId AND f.statut = 'VALIDE'")
    BigDecimal sumNetAPayerByLotId(@Param("lotId") UUID lotId);
    
    @Query("SELECT f FROM FicheDePaie f JOIN FETCH f.lignesFichePaie WHERE f.id = :id")
    Optional<FicheDePaie> findByIdWithLignes(@Param("id") UUID id);
    
    boolean existsByEmployeeIdAndLotBulletinPaieId(UUID employeeId, UUID lotBulletinPaieId);
}
