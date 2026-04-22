package com.kjava.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contrats")
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeId; // Lien vers le module M1

    private String typeContrat; // CDD, CDI, Stage (RG-M4-02)
    
    private LocalDate dateDebut;
    private LocalDate dateFin; // Obligatoire si CDD/Stage (RG-M4-02)

    @Enumerated(EnumType.STRING)
    private StatutContrat statut = StatutContrat.BROUILLON; // RG-M4-01

    @ManyToOne
    private Categorie categorie;

    @ManyToOne
    private Echelon echelon;

    private LocalDateTime dateCreation = LocalDateTime.now();

    // Méthodes métier intégrées (NB2)
    public boolean isModifiable() {
        return this.statut == StatutContrat.BROUILLON; // RG-M4-04
    }

    public enum StatutContrat {
        BROUILLON, EN_COURS, TERMINE, ANNULE
    }

	public String getTypeContrat() {
		return typeContrat;
	}

	public void setTypeContrat(String typeContrat) {
		this.typeContrat = typeContrat;
	}

	public LocalDate getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}

	public LocalDate getDateFin() {
		return dateFin;
	}

	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}

	public StatutContrat getStatut() {
		return statut;
	}

	public void setStatut(StatutContrat statut) {
		this.statut = statut;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public Echelon getEchelon() {
		return echelon;
	}

	public void setEchelon(Echelon echelon) {
		this.echelon = echelon;
	}

	public LocalDateTime getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Long getId() {
		return id;
	}

	public Long getEmployeId() {
		return employeId;
	}
    
}
