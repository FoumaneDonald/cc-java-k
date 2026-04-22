package com.kjava.repositories;

import com.kjava.entities.StructureSalariale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StructureSalarialeRepository extends JpaRepository<StructureSalariale, UUID> {
    
    Optional<StructureSalariale> findByCode(String code);
    
    List<StructureSalariale> findByCategoryId(UUID categoryId);
    
    @Query("SELECT s FROM StructureSalariale s JOIN s.reglesSalariales r WHERE r.id = :regleId")
    List<StructureSalariale> findByRegleSalarialeId(@Param("regleId") UUID regleId);
    
    @Query("SELECT s FROM StructureSalariale s WHERE s.categoryId = :categoryId ORDER BY s.nom")
    List<StructureSalariale> findByCategoryIdOrderByNom(@Param("categoryId") UUID categoryId);
    
    boolean existsByCode(String code);
    
    @Query("SELECT COUNT(s) > 0 FROM StructureSalariale s WHERE s.code = :code AND s.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") UUID id);
    
    @Query("SELECT s FROM StructureSalariale s LEFT JOIN FETCH s.reglesSalariales WHERE s.id = :id")
    Optional<StructureSalariale> findByIdWithRegles(@Param("id") UUID id);
}
