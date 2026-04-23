package com.kjava.models.contrats;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "contrats")
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "operant_id", nullable = true)
    private Employe operant;

    @ManyToOne
    @JoinColumn(name = "chef_service_id")
    private Employe chefService;

    private String typeContrat; // CDD, CDI, Stage

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutContrat statut = StatutContrat.BROUILLON;

    @ManyToOne
    private Categorie categorie;

    @ManyToOne
    private Echelon echelon;

    private LocalDateTime dateCreation = LocalDateTime.now();

    public enum StatutContrat {
        BROUILLON, EN_COURS, TERMINE, ANNULE
    }

    // ── Méthode métier ──────────────────────────────────────────
    public boolean isModifiable() {
        return this.statut == StatutContrat.BROUILLON;
    }

    // ── id ──────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ── operant ─────────────────────────────────────────────────
    public Employe getOperant() { return operant; }
    public void setOperant(Employe operant) { this.operant = operant; }

    // ── chefService ─────────────────────────────────────────────
    public Employe getChefService() { return chefService; }
    public void setChefService(Employe chefService) { this.chefService = chefService; }

    // ── typeContrat ─────────────────────────────────────────────
    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }

    // ── dateDebut ────────────────────────────────────────────────
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    // ── dateFin ──────────────────────────────────────────────────
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    // ── statut ───────────────────────────────────────────────────
    public StatutContrat getStatut() { return statut; }
    public void setStatut(StatutContrat statut) { this.statut = statut; }

    // ── categorie ────────────────────────────────────────────────
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    // ── echelon ──────────────────────────────────────────────────
    public Echelon getEchelon() { return echelon; }
    public void setEchelon(Echelon echelon) { this.echelon = echelon; }

    // ── dateCreation ─────────────────────────────────────────────
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}