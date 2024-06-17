package ru.vsu.cs.dzhabbarov;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CompetitionScanner {

    private static final String COMPETITION = "Компетенции";
    private static final String INDEX = "Индекс";
    private static final String PC_HEADER = "Тип задач проф. деятельности: ";
    private static final int ROOT_INDEX_COLUMN = 0;
    private static final int INDEX_COLUMN = 1;
    private static final int CONTENT_COLUMN = 4;
    private final Workbook excelFile;


    public Map<CompetitionPair, List<CompetitionPair>> getCompetitionMap() {
        Map<CompetitionPair, List<CompetitionPair>> result = new HashMap<>();
        Sheet sheet = excelFile.getSheet(COMPETITION);
        if (sheet == null) {
            log.warn("sheet is null");
        }
        assert sheet != null;
        try {
            boolean isPc = false;
            for (Row row : sheet) {
                Cell rootCell = row.getCell(isPc ? ROOT_INDEX_COLUMN + 1 : ROOT_INDEX_COLUMN);
                Cell cell = row.getCell(isPc ? INDEX_COLUMN + 1 : INDEX_COLUMN);
                Cell content = row.getCell(CONTENT_COLUMN);
                if (rootCell.getCellType() != CellType.STRING && cell.getCellType() != CellType.STRING) {
                    continue;
                }
                if (rootCell.getCellType() == CellType.STRING) {
                    var value = rootCell.getStringCellValue();
                    if (value.equals(INDEX)) {
                        continue;
                    }
                    if (value.equals(PC_HEADER)) {
                        isPc = true;
                        continue;
                    }
                    CompetitionPair pair = new CompetitionPair(value, content.getStringCellValue());
                    result.putIfAbsent(pair, new ArrayList<>());
                } else if (cell.getCellType() == CellType.STRING) {
                    String value = cell.getStringCellValue();
                    String rootValue = value.split("\\.")[0];
                    CompetitionPair rootKey = new CompetitionPair(rootValue, "dummy"); // hashcode, equals только по index

                    var pair = new CompetitionPair(value, content.getStringCellValue());
                    result.get(rootKey).add(pair);
                }
            }
        } catch (NullPointerException e) {
            log.warn(String.valueOf(result.size()));
            throw new RuntimeException(e);
        }
        return result;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class CompetitionPair {

        private String index;

        @EqualsAndHashCode.Exclude
        private String content;
    }

}
