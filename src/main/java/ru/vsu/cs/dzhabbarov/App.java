package ru.vsu.cs.dzhabbarov;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.vsu.cs.dzhabbarov.builder.AnnotationBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class App extends JFrame {

    private final JTextField excelFilePathField1;
    private final JTextField excelFilePathField2;
    private final JTextField outFilePathField;

    public App() {
        setTitle("Competition Scanner");
        setSize(600, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        excelFilePathField1 = new JTextField();
        excelFilePathField2 = new JTextField();
        outFilePathField = new JTextField();
        JButton chooseExcelButton1 = new JButton("Choose Plan Excel");
        JButton chooseExcelButton2 = new JButton("Choose Goals Excel");
        JButton chooseOutButton = new JButton("Choose Output Directory");
        JButton submitButton = new JButton("Submit");

        String currentDir = System.getProperty("user.dir");

        chooseExcelButton1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(currentDir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(App.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                excelFilePathField1.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        chooseExcelButton2.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(currentDir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(App.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                excelFilePathField2.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        chooseOutButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(currentDir);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showSaveDialog(App.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                outFilePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        submitButton.addActionListener(e -> {
            String excelFilePath1 = excelFilePathField1.getText();
            String excelFilePath2 = excelFilePathField2.getText();
            String outDirectoryPath = outFilePathField.getText();

            try (FileInputStream fis1 = new FileInputStream(excelFilePath1);
                 FileInputStream fis2 = new FileInputStream(excelFilePath2);
                 Workbook workbook1 = new XSSFWorkbook(fis1);
                 Workbook workbook2 = new XSSFWorkbook(fis2)) {

                var competitionScanner = new AnnotationBuilder(workbook1, workbook2);
                Path outPath = Paths.get(outDirectoryPath, "out.docx");
                competitionScanner.build(outPath);
                JOptionPane.showMessageDialog(App.this, "Files processed successfully!");
            } catch (Exception ex) {
                log.warn(ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(App.this, "Error processing files: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Учебный план в формате Excel"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(excelFilePathField1, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(chooseExcelButton1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Сгенерированные цели и задачи Excel"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(excelFilePathField2, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(chooseExcelButton2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Output Directory:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(outFilePathField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(chooseOutButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
