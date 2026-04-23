package com.kjava.models.contrats;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // Ex: Cadre, Agent de maîtrise
    private String libelle;

    @OneToMany(mappedBy = "categorie")
    @JsonIgnore   // ← Évite la boucle infinie Categorie→Echelon→Categorie→...
    private List<Echelon> echelons;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public List<Echelon> getEchelons() {
		return echelons;
	}

	public void setEchelons(List<Echelon> echelons) {
		this.echelons = echelons;
	}
    
}