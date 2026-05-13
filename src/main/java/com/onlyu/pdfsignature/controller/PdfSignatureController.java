package com.onlyu.pdfsignature.controller;
import com.onlyu.pdfsignature.dto.PdfSignatureRequest;
import com.onlyu.pdfsignature.service.PdfSignatureService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
public class PdfSignatureController {

    private final PdfSignatureService pdfSignatureService;

    public PdfSignatureController(PdfSignatureService pdfSignatureService) {
        this.pdfSignatureService = pdfSignatureService;
    }

    @PostMapping(
            value = "/sign",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> signPdf(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("request") PdfSignatureRequest request
    ) {
        byte[] signedPdf = pdfSignatureService.signPdf(file, request);

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
}
