package com.kjava.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parametres_paie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametrePaie {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @Column(name = "valeur", nullable = false, precision = 19, scale = 4)
    private BigDecimal valeur;
    
    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
