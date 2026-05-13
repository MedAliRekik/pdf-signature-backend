package com.onlyu.pdfsignature.service;

import com.onlyu.pdfsignature.dto.PdfSignatureRequest;
import org.springframework.web.multipart.MultipartFile;

public interface PdfSignatureService {

    byte[] signPdf(MultipartFile file, PdfSignatureRequest request);
}