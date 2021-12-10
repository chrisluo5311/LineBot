package com.infotran.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 解析pdf工具類
 * @author chris
 */
@Slf4j
@Component
public class PDFBoxUtil {

    /**
     * 解析pdf，回傳文字內容
     * @param urlPath
     * @return String
     * @throws IOException
     * */
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
