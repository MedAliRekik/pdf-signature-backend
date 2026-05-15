package com.onlyu.pdfsignature.service.impl;

import com.onlyu.pdfsignature.dto.PdfSignatureRequest;
import com.onlyu.pdfsignature.dto.SignaturePlacementRequest;
import com.onlyu.pdfsignature.exception.PdfProcessingException;
import com.onlyu.pdfsignature.service.PdfSignatureService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PdfSignatureServiceImpl implements PdfSignatureService {

    private static final Logger logger = LoggerFactory.getLogger(PdfSignatureServiceImpl.class);
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final byte[] PDF_MAGIC_BYTES = "%PDF-".getBytes(StandardCharsets.US_ASCII);
    private static final float SIGNATURE_FONT_SIZE = 18F;
    private static final float TEXT_FONT_SIZE = 12F;

    @Value("${app.pdf.max-file-size-bytes:10485760}")
    private long maxFileSizeBytes;

    @Override
    public byte[] signPdf(MultipartFile file, PdfSignatureRequest request) {
        validateRequest(request);
        validateFile(file);
        logger.info("Validation du fichier PDF réussie: fileName={}, size={} bytes", file.getOriginalFilename(), file.getSize());

        try (PDDocument document = Loader.loadPDF(file.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            for (SignaturePlacementRequest signature : request.signatures()) {
                validateSignaturePlacement(document, signature);
                PDPage page = document.getPage(signature.pageNumber() - 1);
                addSignatureToPage(document, page, signature, request.additionalText());
            }

            document.save(outputStream);
            logger.info("Génération du PDF signé terminée avec {} signature(s)", request.signatures().size());
            return outputStream.toByteArray();

        } catch (IOException e) {
            logger.error("Erreur lors du traitement du fichier PDF", e);
            throw new PdfProcessingException("PDF invalide", e);
        }
    }

    private void validateRequest(PdfSignatureRequest request) {
        if (request == null || request.signatures() == null || request.signatures().isEmpty()) {
            throw new PdfProcessingException("Aucune signature fournie");
        }
    }

    private void validateSignaturePlacement(PDDocument document, SignaturePlacementRequest signature) {
        if (signature == null || signature.pageNumber() == null || signature.x() == null || signature.y() == null) {
            throw new PdfProcessingException("Coordonnées invalides");
        }

        if (!Float.isFinite(signature.x()) || !Float.isFinite(signature.y())) {
            throw new PdfProcessingException("Coordonnées invalides");
        }

        if (signature.pageNumber() < 1 || signature.pageNumber() > document.getNumberOfPages()) {
            throw new PdfProcessingException("Page inexistante");
        }

        if (signature.signerName() == null || signature.signerName().isBlank()) {
            throw new PdfProcessingException("Coordonnées invalides");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PdfProcessingException("Erreur lors de la lecture du fichier");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new PdfProcessingException("Erreur lors de la lecture du fichier");
        }

        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            throw new PdfProcessingException("PDF invalide");
        }

        try {
            byte[] header = file.getBytes();
            if (!hasPdfMagicHeader(header)) {
                throw new PdfProcessingException("PDF invalide");
            }
        } catch (IOException e) {
            throw new PdfProcessingException("Erreur lors de la lecture du fichier", e);
        }
    }

    private boolean hasPdfMagicHeader(byte[] content) {
        if (content.length < PDF_MAGIC_BYTES.length) {
            return false;
        }
        for (int i = 0; i < PDF_MAGIC_BYTES.length; i++) {
            if (content[i] != PDF_MAGIC_BYTES[i]) {
                return false;
            }
        }
        return true;
    }

    private void addSignatureToPage(PDDocument document, PDPage page, SignaturePlacementRequest signature, String additionalText)
            throws IOException {

        try (PDPageContentStream contentStream = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
        )) {
            PDType1Font signatureFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
            PDType1Font textFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            contentStream.beginText();
            contentStream.setFont(signatureFont, SIGNATURE_FONT_SIZE);
            contentStream.newLineAtOffset(signature.x(), signature.y());
            contentStream.showText(signature.signerName());
            contentStream.endText();

            if (additionalText != null && !additionalText.isBlank()) {
                contentStream.beginText();
                contentStream.setFont(textFont, TEXT_FONT_SIZE);
                contentStream.newLineAtOffset(signature.x(), signature.y() - 20);
                contentStream.showText(additionalText);
                contentStream.endText();
            }
        }
    }
}
