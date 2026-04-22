package com.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GRH — Système de Gestion des Ressources Humaines")
                        .description("""
                                ## Module M2 — Gestion des Congés
                                
                                API REST pour la gestion complète du cycle de vie des demandes de congé.
                                
                                ### Workflow d'approbation (RG-M2-07)
                                ```
                                DRAFT → SUBMITTED → VALIDATED → CONFIRMED
                                                 ↘              ↘
                                                  REJECTED       REJECTED
                                ```
                                
                                ### Règles métier clés
                                - **RG-M2-01** : La soumission est bloquée si le solde est insuffisant
                                - **RG-M2-04** : Aucun chevauchement de dates avec une demande active
                                - **RG-M2-07** : Workflow à deux niveaux (Chef → RH Manager)
                                - **RG-M2-09** : Le solde est débité uniquement à la confirmation finale
                                
                                ### Intégration inter-modules
                                - Consomme **M1** (statut employé) avant toute soumission
                                - Expose `/absences` pour **M3** (calcul de la paie)
                                - Reçoit la catégorie/convention de **M4** (contrats)
                                
                                ### Format des réponses
                                Toutes les réponses suivent l'enveloppe standard :
                                ```json
                                { "success": true, "data": { ... }, "meta": { "page": 1, "total": 50 } }
                                { "success": false, "error": { "code": "VALIDATION_ERROR", "message": "..." } }
                                ```
                                """)
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Équipe GRH")
                                .email("grh@groupe-grh.cm"))
                        .license(new License()
                                .name("Confidentiel — Groupe GRH 2025")))

                .externalDocs(new ExternalDocumentation()
                        .description("Cahier des Charges Technique v2.0")
                        .url("https://groupe-grh.cm/docs/cdc-v2"))

                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Développement local"),
                        new Server().url("https://api.grh.cm").description("Production")))

                // JWT Bearer — wired by the auth teammate
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenu via le module d'authentification (M1-Auth). Format : Bearer <token>")))

                // Tag groupings — one per controller
                .tags(List.of(
                        new Tag().name("Demandes de Congé")
                                .description("Cycle de vie complet d'une demande : création, soumission, validation Chef, confirmation RH, annulation"),
                        new Tag().name("Soldes de Congé")
                                .description("Consultation et ajustement des soldes par employé et par année. Initialisation prorata pour les nouveaux employés (RG-M2-15)"),
                        new Tag().name("Types de Congé")
                                .description("Catalogue des types de congé disponibles (CP, CM, MAT, CSS…) avec leurs règles (RG-M2-11)"),
                        new Tag().name("Conventions")
                                .description("Conventions collectives qui peuvent surcharger les règles par défaut d'un type de congé (RG-M2-12)"),
                        new Tag().name("Paramètres Congé")
                                .description("Configuration système : jours fériés, mode de calcul, délai de prévenance (RG-M2-14)"),
                        new Tag().name("Intégration M3")
                                .description("Endpoint consommé par le module Paie (M3) pour récupérer les absences confirmées sur une période donnée")
                ));
    }
}
