package com.kjava.entities;

import com.kjava.enums.StatutBulletin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fiches_de_paie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheDePaie {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "salaire_de_base", nullable = false, precision = 19, scale = 4)
    private BigDecimal salaireDeBase;
    
    @Column(name = "total_brut", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalBrut;
    
    @Column(name = "total_retenues", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalRetenues;
    
    @Column(name = "total_charges_salariales", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalChargesSalariales;
    
    @Column(name = "net_a_payer", nullable = false, precision = 19, scale = 4)
    private BigDecimal netAPayer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutBulletin statut;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_bulletin_paie_id", nullable = false)
    private LotBulletinPaie lotBulletinPaie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_salariale_id", nullable = false)
    private StructureSalariale structureSalariale;
    
    @OneToMany(mappedBy = "ficheDePaie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneFichePaie> lignesFichePaie;
    
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
