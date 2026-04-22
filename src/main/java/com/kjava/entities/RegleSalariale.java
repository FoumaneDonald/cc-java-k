package com.kjava.entities;

import com.kjava.enums.TypeRegle;
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
@Table(name = "regles_salariales", uniqueConstraints = {
    @UniqueConstraint(columnNames = "code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegleSalariale {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;
    
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TypeRegle type;
    
    @Column(name = "formule", nullable = false, length = 500)
    private String formule;
    
    @Column(name = "plafond", precision = 19, scale = 4)
    private BigDecimal plafond;
    
    @Column(name = "is_recurrente", nullable = false)
    private Boolean isRecurrente;
    
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
