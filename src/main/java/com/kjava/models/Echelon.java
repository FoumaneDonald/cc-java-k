package com.kjava.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "echelons")
public class Echelon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private Double indiceSalarial; // RG-M4-09

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getIndiceSalarial() {
		return indiceSalarial;
	}

	public void setIndiceSalarial(Double indiceSalarial) {
		this.indiceSalarial = indiceSalarial;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

    
}