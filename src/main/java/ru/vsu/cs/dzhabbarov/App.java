package ru.vsu.cs.dzhabbarov;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.vsu.cs.dzhabbarov.builder.AnnotationBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class App {

    public static void main(String[] args) {
        String excelFilePath = "src/main/resources/src.xlsx";
        String out = "src/main/resources/out.docx";

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            var competitionScanner = new AnnotationBuilder(workbook);

            competitionScanner.build(Paths.get(out));
        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }
}
