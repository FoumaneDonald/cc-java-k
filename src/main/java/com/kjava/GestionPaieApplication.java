package com.kjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Classe principale de l'application GestionPaie
 * * Cette classe configure et démarre l'application Spring Boot avec :
 * - @SpringBootApplication : Configuration principale Spring Boot
 * - @EnableTransactionManagement : Gestion des transactions
 * - @EnableAsync : Support des opérations asynchrones
 * - @EnableCaching : (Désactivé) Activation du cache (nécessite un CacheManager)
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
// @EnableCaching // Désactivé car il manque un Bean CacheManager pour le moment
public class GestionPaieApplication {

	/**
	 * Point d'entrée principal de l'application
	 * * @param args Arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		// Configuration personnalisée du banner pour un démarrage professionnel
		System.setProperty("spring.banner.location", "classpath:banner.txt");
		
		SpringApplication app = new SpringApplication(GestionPaieApplication.class);
		
		// Configuration des propriétés de démarrage
		app.setAdditionalProfiles("dev");
		
		// Démarrage de l'application
		app.run(args);
	}

}