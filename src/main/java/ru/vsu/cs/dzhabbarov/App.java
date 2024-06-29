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

    private final JTextField excelFilePathField;
    private final JTextField outFilePathField;

    public App() {
        setTitle("Competition Scanner");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        excelFilePathField = new JTextField();
        outFilePathField = new JTextField();
        JButton chooseExcelButton = new JButton("Choose Excel File");
        JButton chooseOutButton = new JButton("Choose Output Directory");
        JButton submitButton = new JButton("Submit");

        String currentDir = System.getProperty("user.dir");

        chooseExcelButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(currentDir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(App.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                excelFilePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
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
            String excelFilePath = excelFilePathField.getText();
            String outDirectoryPath = outFilePathField.getText();

            try (FileInputStream fis = new FileInputStream(excelFilePath);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                var competitionScanner = new AnnotationBuilder(workbook);
                Path outPath = Paths.get(outDirectoryPath, "out.docx");
                competitionScanner.build(outPath);
                JOptionPane.showMessageDialog(App.this, "File processed successfully!");
            } catch (Exception ex) {
                log.warn(ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(App.this, "Error processing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Excel File Path:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(excelFilePathField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(chooseExcelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Output Directory:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(outFilePathField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(chooseOutButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
