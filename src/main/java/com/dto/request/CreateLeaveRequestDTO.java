package com.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Corps de requête pour créer ou modifier une demande de congé")
public class CreateLeaveRequestDTO {

    @NotNull(message = "employeeId is required")
    @Schema(description = "UUID de l'employé (fourni par M1)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID employeeId;

    @NotNull(message = "leaveTypeId is required")
    @Schema(description = "UUID du type de congé", example = "7b9e4c21-1234-4abc-8def-000000000001")
    private UUID leaveTypeId;

    @NotNull(message = "startDate is required")
    @FutureOrPresent(message = "startDate must be today or in the future")
    @Schema(description = "Date de début du congé (yyyy-MM-dd)", example = "2025-08-04")
    private LocalDate startDate;

    @NotNull(message = "endDate is required")
    @Schema(description = "Date de fin du congé (yyyy-MM-dd). Doit être ≥ startDate (RG-M2-02)", example = "2025-08-08")
    private LocalDate endDate;

    @Schema(description = "Chemin vers la pièce justificative (requis selon le type de congé, RG-M2-11)", nullable = true, example = "/uploads/certificats/cert_2025_08.pdf")
    private String justificationPath;
}
