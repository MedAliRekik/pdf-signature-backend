package com.onlyu.pdfsignature.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PdfSignatureRequest(
        @NotBlank(message = "Le nom du signataire est obligatoire")
        String signerName,

        String additionalText,

        @Min(value = 1, message = "Le numéro de page doit être supérieur ou égal à 1")
        int pageNumber,

        @Min(value = 0, message = "La position X doit être positive")
        float x,

        @Min(value = 0, message = "La position Y doit être positive")
        float y
) {
}