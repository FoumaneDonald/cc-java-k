package com.kjava.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lignes_fiche_paie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneFichePaie {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "montant_base", nullable = false, precision = 19, scale = 4)
    private BigDecimal montantBase;
    
    @Column(name = "taux", precision = 19, scale = 4)
    private BigDecimal taux;
    
    @Column(name = "montant_calcule", nullable = false, precision = 19, scale = 4)
    private BigDecimal montantCalcule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiche_de_paie_id", nullable = false)
    private FicheDePaie ficheDePaie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regle_salariale_id", nullable = false)
    private RegleSalariale regleSalariale;
    
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
