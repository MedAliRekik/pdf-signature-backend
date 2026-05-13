package com.onlyu.pdfsignature.service.impl;

import com.onlyu.pdfsignature.dto.PdfSignatureRequest;
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

            validatePageNumber(document, request.pageNumber());
            logger.info("Validation des paramètres de signature réussie: pageNumber={}, x={}, y={}",
                    request.pageNumber(), request.x(), request.y());

            PDPage page = document.getPage(request.pageNumber() - 1);
            addTextToPage(document, page, request);

            document.save(outputStream);
            logger.info("Génération du PDF signé terminée");
            return outputStream.toByteArray();

        } catch (IOException e) {
            logger.error("Erreur lors du traitement du fichier PDF", e);
            throw new PdfProcessingException("Erreur lors du traitement du fichier PDF", e);
        }
    }

    private void validateRequest(PdfSignatureRequest request) {
        if (request == null) {
            throw new PdfProcessingException("Les paramètres de signature sont obligatoires");
        }
        if (!Float.isFinite(request.x()) || !Float.isFinite(request.y())) {
            throw new PdfProcessingException("Les coordonnées de signature sont invalides");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PdfProcessingException("Le fichier PDF est obligatoire");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new PdfProcessingException("Le fichier PDF dépasse la taille maximale autorisée");
        }

        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            throw new PdfProcessingException("Le fichier doit être au format PDF");
        }

        try {
            byte[] header = file.getBytes();
            if (!hasPdfMagicHeader(header)) {
                throw new PdfProcessingException("Le contenu du fichier n'est pas un PDF valide");
            }
        } catch (IOException e) {
            throw new PdfProcessingException("Impossible de lire le fichier PDF", e);
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

    private void validatePageNumber(PDDocument document, int pageNumber) {
        if (pageNumber < 1 || pageNumber > document.getNumberOfPages()) {
            throw new PdfProcessingException("Le numéro de page est invalide");
        }
    }

    private void addTextToPage(PDDocument document, PDPage page, PdfSignatureRequest request) throws IOException {

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
            contentStream.newLineAtOffset(request.x(), request.y());
            contentStream.showText(request.signerName());
            contentStream.endText();

            if (request.additionalText() != null && !request.additionalText().isBlank()) {
                contentStream.beginText();
                contentStream.setFont(textFont, TEXT_FONT_SIZE);
                contentStream.newLineAtOffset(request.x(), request.y() - 20);
                contentStream.showText(request.additionalText());
                contentStream.endText();
            }
        }
    }
}
