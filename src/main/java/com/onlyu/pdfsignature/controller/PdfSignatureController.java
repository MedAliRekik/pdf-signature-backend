package com.onlyu.pdfsignature.controller;

import tools.jackson.databind.ObjectMapper;
import com.onlyu.pdfsignature.dto.PdfSignatureRequest;
import com.onlyu.pdfsignature.exception.PdfProcessingException;
import com.onlyu.pdfsignature.service.PdfSignatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
@Tag(name = "PDF Signature", description = "API de signature visuelle de PDF")
public class PdfSignatureController {

    private static final Logger logger = LoggerFactory.getLogger(PdfSignatureController.class);

    private final PdfSignatureService pdfSignatureService;
    private final ObjectMapper objectMapper;

    public PdfSignatureController(PdfSignatureService pdfSignatureService, ObjectMapper objectMapper) {
        this.pdfSignatureService = pdfSignatureService;
        this.objectMapper = objectMapper;
    }

    @Operation(
            summary = "Signer un PDF",
            description = "Ajoute une ou plusieurs signatures visuelles et un texte optionnel dans un PDF.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = PdfSignatureMultipartSchema.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF signé généré",
                            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE,
                                    schema = @Schema(type = "string", format = "binary"))),
                    @ApiResponse(responseCode = "400", description = "Requête invalide"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne")
            }
    )
    @PostMapping(
            value = "/sign",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> signPdf(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") String requestJson
    ) {
        PdfSignatureRequest request = parseRequest(requestJson);

        logger.info("Réception d'une demande de signature PDF: fileName={}, signatures={}, hasAdditionalText={}",
                file != null ? file.getOriginalFilename() : null,
                request != null && request.signatures() != null ? request.signatures().size() : null,
                request != null && request.additionalText() != null && !request.additionalText().isBlank());

        byte[] signedPdf = pdfSignatureService.signPdf(file, request);

        logger.info("PDF signé généré avec succès: fileName={}", file.getOriginalFilename());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("signed-document.pdf")
                                .build()
                                .toString()
                )
                .body(signedPdf);
    }

    private PdfSignatureRequest parseRequest(String requestJson) {
        if (requestJson == null || requestJson.isBlank()) {
            throw new PdfProcessingException("Aucune signature fournie");
        }
        try {
            return objectMapper.readValue(requestJson, PdfSignatureRequest.class);
        } catch (Exception e) {
            throw new PdfProcessingException("Le JSON du champ request est invalide", e);
        }
    }

    private record PdfSignatureMultipartSchema(
            @Schema(type = "string", format = "binary", description = "Fichier PDF à signer")
            String file,
            @Schema(description = "Paramètres de signature JSON")
            String request
    ) {
    }
}
