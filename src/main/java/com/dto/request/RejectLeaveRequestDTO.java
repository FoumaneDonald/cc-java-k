package com.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Corps de requête pour rejeter une demande — motif obligatoire (RG-M2-08)")
public class RejectLeaveRequestDTO {

    @NotBlank(message = "rejectionReason is required when rejecting")
    @Schema(description = "Motif du rejet — obligatoire, notifié à l'employé", example = "Équipe déjà en sous-effectif sur cette période")
    private String rejectionReason;
}
