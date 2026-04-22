package com.kjava.repositories;

import com.kjava.entities.LotBulletinPaie;
import com.kjava.enums.StatutBulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LotBulletinPaieRepository extends JpaRepository<LotBulletinPaie, UUID> {
    
    List<LotBulletinPaie> findByMoisAndAnnee(Integer mois, Integer annee);
    
    List<LotBulletinPaie> findByStatut(StatutBulletin statut);
    
    List<LotBulletinPaie> findByMoisAndAnneeAndStatut(Integer mois, Integer annee, StatutBulletin statut);
    
    @Query("SELECT l FROM LotBulletinPaie l WHERE l.annee = :annee ORDER BY l.mois DESC")
    List<LotBulletinPaie> findByAnneeOrderByMoisDesc(@Param("annee") Integer annee);
    
    @Query("SELECT l FROM LotBulletinPaie l WHERE l.statut = :statut ORDER BY l.annee DESC, l.mois DESC")
    List<LotBulletinPaie> findByStatutOrderByAnneeMoisDesc(@Param("statut") StatutBulletin statut);
    
    @Query("SELECT COUNT(l) FROM LotBulletinPaie l WHERE l.mois = :mois AND l.annee = :annee AND l.statut = 'VALIDE'")
    long countValidatedByMonthAndYear(@Param("mois") Integer mois, @Param("annee") Integer annee);
    
    boolean existsByMoisAndAnnee(Integer mois, Integer annee);
    
    @Query("SELECT EXISTS (SELECT 1 FROM LotBulletinPaie l WHERE l.mois = :mois AND l.annee = :annee AND l.statut = 'VALIDE')")
    boolean existsValidatedByMonthAndYear(@Param("mois") Integer mois, @Param("annee") Integer annee);
}
