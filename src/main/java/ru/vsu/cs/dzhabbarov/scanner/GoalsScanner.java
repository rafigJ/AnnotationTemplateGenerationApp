package ru.vsu.cs.dzhabbarov.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GoalsScanner {

    private final Workbook excelFile;

    public Map<String, String> getIndexGoalsMap() {
        Map<String, String> result = new HashMap<>();
        Sheet sheet = excelFile.getSheetAt(0);
        if (sheet == null) {
            log.warn("sheet is null");
            throw new NullPointerException("sheet is null");
        }
        try {
            for (Row row : sheet) {
                result.put(row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue());
            }
        } catch (NullPointerException e) {
            log.warn(String.valueOf(result.size()));
            throw new RuntimeException(e);
        }
        return result;
    }
}