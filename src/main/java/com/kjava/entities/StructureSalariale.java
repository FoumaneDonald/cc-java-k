package com.kjava.entities;

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
@Table(name = "structures_salariales", indexes = {
    @Index(name = "idx_structure_code", columnList = "code"),
    @Index(name = "idx_structure_category_id", columnList = "categoryId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StructureSalariale {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "structure_regles",
        joinColumns = @JoinColumn(name = "structure_id"),
        inverseJoinColumns = @JoinColumn(name = "regle_id")
    )
    @OrderColumn(name = "ordre_calcul")
    private List<RegleSalariale> reglesSalariales;
    
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
