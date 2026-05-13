package com.onlyu.pdfsignature.service.impl;

import com.onlyu.pdfsignature.dto.PdfSignatureRequest;
import com.onlyu.pdfsignature.exception.PdfProcessingException;
import com.onlyu.pdfsignature.service.PdfSignatureService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
public class PdfSignatureServiceImpl implements PdfSignatureService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final float SIGNATURE_FONT_SIZE = 18F;
    private static final float TEXT_FONT_SIZE = 12F;

    @Override
    public byte[] signPdf(MultipartFile file, PdfSignatureRequest request) {
        validateFile(file);

        try (PDDocument document = Loader.loadPDF(file.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            validatePageNumber(document, request.pageNumber());

            PDPage page = document.getPage(request.pageNumber() - 1);

            addTextToPage(document, page, request);

            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new PdfProcessingException("Erreur lors du traitement du fichier PDF", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PdfProcessingException("Le fichier PDF est obligatoire");
        }

        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            throw new PdfProcessingException("Le fichier doit être au format PDF");
        }
    }

    private void validatePageNumber(PDDocument document, int pageNumber) {
        if (pageNumber < 1 || pageNumber > document.getNumberOfPages()) {
            throw new PdfProcessingException("Le numéro de page est invalide");
        }
    }

    private void addTextToPage(
            PDDocument document,
            PDPage page,
            PdfSignatureRequest request
    ) throws IOException {

        try (PDPageContentStream contentStream = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
        )) {
            PDType1Font signatureFont =
                    new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);

            PDType1Font textFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA);

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