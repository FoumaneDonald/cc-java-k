package com.kjava.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Module M3 - Gestion de Paie")
                        .version("2.0")
                        .description("API REST pour la gestion de la paie du Module M3 de l'ERP RH. Cette API permet de gérer les paramètres de paie, les règles salariales, les structures salariales, les lots de bulletin de paie, les fiches de paie et les lignes de fiche de paie.")
                        .contact(new Contact()
                                .name("Équipe de développement")
                                .email("dev@kjava.com")
                                .url("https://www.kjava.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.kjava.com")
                                .description("Serveur de production")
                ));
    }
}
