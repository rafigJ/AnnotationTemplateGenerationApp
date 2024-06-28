package ru.vsu.cs.dzhabbarov;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.vsu.cs.dzhabbarov.scanner.CompetitionPair;
import ru.vsu.cs.dzhabbarov.scanner.CompetitionScanner;
import ru.vsu.cs.dzhabbarov.scanner.DisciplineScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class App {

    public static final String COMPETITION_2 = "Компетенции(2)";
    public static final String PLAN = "План";
    public static final String COMPETITION = "Компетенции";

    public static void main(String[] args) {
        String excelFilePath = "src/main/resources/src.xlsx";

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            var competitionScanner = new DisciplineScanner(workbook);
            competitionScanner.getDisciplineList()
                    .forEach(value -> {
                        String root = "";
                        List<String> subRoot = new ArrayList<>();
                        for (Map.Entry<CompetitionPair, List<CompetitionPair>> entry : value.competitionPairListMap().entrySet()) {
                            root = entry.getKey().getIndex() + " " + entry.getKey().getContent();
                            for (CompetitionPair competitionPair : entry.getValue()) {
                                subRoot.add("- " + competitionPair.getIndex() + " " + competitionPair.getContent());
                            }
                        }
                        log.info(" {} {}", value.index(), value.disciplineName());
                        log.info(" \t {}", root);
                        for (String s : subRoot) {
                            log.info(" \t\t {}",s);
                        }
                    });

        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }
}
