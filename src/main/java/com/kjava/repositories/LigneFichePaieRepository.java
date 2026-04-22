package com.kjava.repositories;

import com.kjava.entities.LigneFichePaie;
import com.kjava.entities.RegleSalariale;
import com.kjava.enums.TypeRegle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface LigneFichePaieRepository extends JpaRepository<LigneFichePaie, UUID> {
    
    List<LigneFichePaie> findByFicheDePaieId(UUID ficheDePaieId);
    
    List<LigneFichePaie> findByRegleSalarialeId(UUID regleSalarialeId);
    
    @Query("SELECT l FROM LigneFichePaie l WHERE l.ficheDePaie.id = :ficheId ORDER BY l.regleSalariale.nom")
    List<LigneFichePaie> findByFicheIdOrderByRegleNom(@Param("ficheId") UUID ficheId);
    
    @Query("SELECT l FROM LigneFichePaie l JOIN l.regleSalariale r WHERE l.ficheDePaie.id = :ficheId AND r.type = :type")
    List<LigneFichePaie> findByFicheIdAndRegleType(@Param("ficheId") UUID ficheId, @Param("type") TypeRegle type);
    
    @Query("SELECT SUM(l.montantCalcule) FROM LigneFichePaie l WHERE l.ficheDePaie.id = :ficheId AND l.regleSalariale.type IN :types")
    BigDecimal sumMontantCalculeByFicheIdAndTypes(@Param("ficheId") UUID ficheId, @Param("types") List<TypeRegle> types);
    
    @Query("SELECT SUM(l.montantCalcule) FROM LigneFichePaie l WHERE l.ficheDePaie.id = :ficheId AND l.regleSalariale.type = 'CHARGE_SALARIALE'")
    BigDecimal sumChargesSalarialesByFicheId(@Param("ficheId") UUID ficheId);
    
    @Query("SELECT SUM(l.montantCalcule) FROM LigneFichePaie l WHERE l.ficheDePaie.id = :ficheId AND l.regleSalariale.type IN ('RETENUE', 'CHARGE_SALARIALE')")
    BigDecimal sumRetenuesByFicheId(@Param("ficheId") UUID ficheId);
    
    @Query("SELECT l FROM LigneFichePaie l WHERE l.ficheDePaie.id = :ficheId AND l.regleSalariale.type IN ('PRIME', 'INDEMNITE')")
    List<LigneFichePaie> findPrimesAndIndemnitesByFicheId(@Param("ficheId") UUID ficheId);
    
    @Query("SELECT l FROM LigneFichePaie l WHERE l.ficheDePaie.id = :ficheId AND l.regleSalariale.type = 'ABSENCE'")
    List<LigneFichePaie> findAbsencesByFicheId(@Param("ficheId") UUID ficheId);
    
    boolean existsByFicheDePaieIdAndRegleSalarialeId(UUID ficheDePaieId, UUID regleSalarialeId);
}
