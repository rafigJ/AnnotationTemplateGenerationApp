package ru.vsu.cs.dzhabbarov;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class App {

    public static final String COMPETITION_2 = "Компетенции(2)";
    public static final String PLAN = "План";
    public static final String COMPETITION = "Компетенции";

    public static void main(String[] args) {
        String excelFilePath = "src/main/resources/src.xlsx";
        String[] sheetsToRead = {COMPETITION_2};

        try (FileInputStream fis = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(fis)) {
            var competitionScanner = new CompetitionScanner(workbook);
            competitionScanner.getCompetitionMap()
                    .forEach((key, value) -> {
                        String indexList = value.stream().map(CompetitionScanner.CompetitionPair::getIndex).collect(Collectors.joining(", "));
                        log.info(" {}: {}", key.getIndex(), indexList);
                    });

        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }
}
