package com.onlyu.pdfsignature.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PdfSignatureRequest(
        @Schema(description = "Nom du signataire", example = "Ali Rekik")
        @NotBlank(message = "Le nom du signataire est obligatoire")
        @Size(max = 120, message = "Le nom du signataire est trop long")
        String signerName,

        @Schema(description = "Texte additionnel optionnel", example = "Bon pour accord")
        @Size(max = 240, message = "Le texte additionnel est trop long")
        String additionalText,

        @Schema(description = "Numéro de page (>= 1)", example = "1")
        @Min(value = 1, message = "Le numéro de page doit être supérieur ou égal à 1")
        int pageNumber,

        @Schema(description = "Position X (>= 0)", example = "120")
        @PositiveOrZero(message = "La position X doit être positive ou nulle")
        float x,

        @Schema(description = "Position Y (>= 0)", example = "140")
        @PositiveOrZero(message = "La position Y doit être positive ou nulle")
        float y
) {
}
