package com.onlyu.pdfsignature.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SignaturePlacementRequest(
        @Schema(description = "Nom du signataire", example = "Ali Rekik")
        @NotBlank(message = "Le nom du signataire est obligatoire")
        @Size(max = 120, message = "Le nom du signataire est trop long")
        String signerName,

        @Schema(description = "Numéro de page (>= 1)", example = "1")
        @Min(value = 1, message = "Le numéro de page doit être supérieur ou égal à 1")
        Integer pageNumber,

        @Schema(description = "Position X (>= 0)", example = "120")
        @PositiveOrZero(message = "La position X doit être positive ou nulle")
        Float x,

        @Schema(description = "Position Y (>= 0)", example = "140")
        @PositiveOrZero(message = "La position Y doit être positive ou nulle")
        Float y,

        @Schema(description = "Largeur de la zone de signature (> 0)", example = "180")
        @Positive(message = "La largeur doit être strictement positive")
        Float width,

        @Schema(description = "Hauteur de la zone de signature (> 0)", example = "60")
        @Positive(message = "La hauteur doit être strictement positive")
        Float height
) {
}
