package com.onlyu.pdfsignature.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PdfSignatureRequest(
        @Schema(description = "Liste des signatures à apposer")
        @NotEmpty(message = "Aucune signature fournie")
        List<@Valid SignaturePlacementRequest> signatures,

        @Schema(description = "Texte additionnel optionnel", example = "Bon pour accord")
        @Size(max = 240, message = "Le texte additionnel est trop long")
        String additionalText
) {
}
