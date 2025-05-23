package com.jobdam.jobdam_be.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class PDFProvider {

    public static String pdfToString(MultipartFile pdf) {
        try (InputStream inputStream = new ByteArrayInputStream(pdf.getBytes());
             PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            log.error("PDF 처리 실패", e);
            return "텍스트 추출 실패: " + e.getMessage();
        }
    }
}
