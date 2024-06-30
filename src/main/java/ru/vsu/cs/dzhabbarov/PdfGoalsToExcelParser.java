package ru.vsu.cs.dzhabbarov;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PdfGoalsToExcelParser {

    public static String extractTextBetweenSections(String text, String startSection, String endSection) {
        int startIndex = text.indexOf(startSection);
        int endIndex = text.indexOf(endSection, startIndex);
        if (startIndex == -1 || endIndex == -1) {
            return null;
        }
        return text.substring(startIndex + startSection.length(), endIndex).trim();
    }

    public static String extractIndex(String text) {
        int startIndex = text.indexOf("РАБОЧАЯ ПРОГРАММА УЧЕБНОЙ ДИСЦИПЛИНЫ");
        if (startIndex == -1) {
            return null;
        }
        startIndex = text.indexOf("\n", startIndex);
        startIndex = text.indexOf("Б1.", startIndex);
        if (startIndex == -1) {
            return null;
        }
        int indexEnd = text.indexOf(" ", startIndex);
        if (indexEnd - startIndex < 4) {
            indexEnd = text.indexOf(" ", indexEnd + 1);
        }
        if (indexEnd == -1) {
            return null;
        }
        String trim = text.substring(startIndex, indexEnd).trim();
        return removeTrailingDot(trim.replace(" ", ""));
    }

    public static void main(String[] args) {
        String pdfFolderPath = "D:\\Java\\Прога ВУЗ\\6 семестр\\AnnotationTemplateGenerationApp\\src\\main\\resources\\goals";
        File pdfFolder = new File(pdfFolderPath);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null) {
            System.out.println("No PDF files found in the specified folder.");
            return;
        }

        Map<String, String> dataMap = new HashMap<>();

        for (File pdfFile : pdfFiles) {
            try (PDDocument document = PDDocument.load(pdfFile)) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfStripper.setStartPage(1);
                pdfStripper.setEndPage(3);
                String fullText = pdfStripper.getText(document);

                String index = extractIndex(fullText);
                String extractedText = extractTextBetweenSections(fullText, "9.Цели и задачи учебной дисциплины:", "10.");
                if (extractedText == null) {
                    extractedText = extractTextBetweenSections(fullText, "9. Цели и задачи учебной дисциплины:", "10.");
                }
                if (extractedText == null) {
                    extractedText = extractTextBetweenSections(fullText, "9. Цели и задачи учебной дисциплины ", "10.");
                }
                if (extractedText == null) {
                    extractedText = extractTextBetweenSections(fullText, "9. Цели и задачи учебной дисциплины", "10.");
                }
                if (extractedText == null) {
                    extractedText = extractTextBetweenSections(fullText, "9.Цели и задачи учебной дисциплины", "10.");
                }
                if (extractedText == null) {
                    extractedText = extractTextBetweenSections(fullText, "10. Цели и задачи учебной дисциплины ", "11.");
                }
                if (index != null && extractedText != null) {
                    dataMap.put(index, extractedText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("PDF Data");

            int rowNum = 0;
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            try (FileOutputStream fileOut = new FileOutputStream("goals.xlsx")) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Data successfully saved to goals.xlsx");
    }

    public static String removeTrailingDot(String str) {
        if (str.endsWith(".")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
