package com.infotran.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@Slf4j
public class PDFBoxUtil {

    private static final String LOG_PREFIX = "PdfBoxUtil";

    public static String readPDF(String urlPath) throws IOException {
        URL pdfUrl = new URL(urlPath);
        InputStream inputStream = pdfUrl.openStream();
        //Loading an existing document
        PDDocument document = PDDocument.load(inputStream);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);
        //Closing the document
        document.close();
        return text;
    }

}
