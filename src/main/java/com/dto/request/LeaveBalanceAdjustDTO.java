package com.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Corps de requête pour créer ou ajuster le solde d'un employé")
public class LeaveBalanceAdjustDTO {

    @NotNull
    @Schema(description = "UUID de l'employé (M1)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID employeeId;

    @NotNull
    @Schema(description = "UUID du type de congé", example = "7b9e4c21-1234-4abc-8def-000000000001")
    private UUID leaveTypeId;

    @NotNull
    @Schema(description = "Année du solde", example = "2025")
    private Integer year;

    @NotNull
    @Positive(message = "totalDays must be positive")
    @Schema(description = "Nombre total de jours alloués pour cette année", example = "30.0")
    private BigDecimal totalDays;
}
