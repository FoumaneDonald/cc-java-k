package com.kjava.repositories;

import com.kjava.entities.RegleSalariale;
import com.kjava.enums.TypeRegle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegleSalarialeRepository extends JpaRepository<RegleSalariale, UUID> {
    
    Optional<RegleSalariale> findByCode(String code);
    
    List<RegleSalariale> findByType(TypeRegle type);
    
    List<RegleSalariale> findByIsRecurrenteTrue();
    
    List<RegleSalariale> findByIsRecurrenteFalse();
    
    @Query("SELECT r FROM RegleSalariale r WHERE r.type = :type AND r.isRecurrente = :isRecurrente")
    List<RegleSalariale> findByTypeAndIsRecurrente(@Param("type") TypeRegle type, @Param("isRecurrente") Boolean isRecurrente);
    
    @Query("SELECT r FROM RegleSalariale r WHERE r.plafond IS NOT NULL")
    List<RegleSalariale> findWithPlafond();
    
    boolean existsByCode(String code);
    
    @Query("SELECT COUNT(r) > 0 FROM RegleSalariale r WHERE r.code = :code AND r.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") UUID id);
}
