package ru.vsu.cs.dzhabbarov;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PdfGoalsToExcelParser extends JFrame {

    private final JTextField pdfFolderPathField;
    private final JTextField savePathField;

    public PdfGoalsToExcelParser() {
        setTitle("PDF to Excel Parser");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        pdfFolderPathField = new JTextField();
        savePathField = new JTextField();
        JButton choosePdfFolderButton = new JButton("Select PDF Directory");
        JButton chooseSaveFolderButton = new JButton("Select Output Directory");
        JButton submitButton = new JButton("Submit");

        String currentDir = System.getProperty("user.dir");

        choosePdfFolderButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(currentDir);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(PdfGoalsToExcelParser.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                pdfFolderPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        chooseSaveFolderButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(currentDir);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showSaveDialog(PdfGoalsToExcelParser.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                savePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        submitButton.addActionListener(e -> {
            String pdfFolderPath = pdfFolderPathField.getText();
            String saveDirectoryPath = savePathField.getText();

            processPdfFiles(pdfFolderPath, saveDirectoryPath);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("PDF Folder Path:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(pdfFolderPathField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(choosePdfFolderButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Output Directory:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(savePathField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(chooseSaveFolderButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
    }

    private void processPdfFiles(String pdfFolderPath, String savePath) {
        File pdfFolder = new File(pdfFolderPath);
        File[] pdfFiles = pdfFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null) {
            JOptionPane.showMessageDialog(this, "No PDF files found in the specified folder.");
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
} catch (Exception e) {
    log.warn(e.getMessage());
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error reading PDF file: " + pdfFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
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

            try (FileOutputStream fileOut = new FileOutputStream(savePath + "/goals.xlsx")) {
                workbook.write(fileOut);
            }

            JOptionPane.showMessageDialog(this, "Data successfully saved to " + savePath + "/goals.xlsx");

        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving Excel file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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

    public static String removeTrailingDot(String str) {
        if (str.endsWith(".")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PdfGoalsToExcelParser().setVisible(true));
    }
}
