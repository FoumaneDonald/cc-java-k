package com.kjava.entities;

import com.kjava.enums.StatutBulletin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lots_bulletin_paie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotBulletinPaie {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "mois", nullable = false)
    private Integer mois;
    
    @Column(name = "annee", nullable = false)
    private Integer annee;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutBulletin statut;
    
    @Column(name = "date_generation")
    private LocalDateTime dateGeneration;
    
    @OneToMany(mappedBy = "lotBulletinPaie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FicheDePaie> fichesDePaie;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateGeneration == null) {
            dateGeneration = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
