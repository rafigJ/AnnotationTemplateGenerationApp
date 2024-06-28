package ru.vsu.cs.dzhabbarov.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import ru.vsu.cs.dzhabbarov.scanner.enums.AttestationType;
import ru.vsu.cs.dzhabbarov.scanner.records.DisciplinePlanInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class PlanScanner {

    private final Workbook excelFile;
    private static final String PLAN = "План";

    private static final int DISCIPLINE_NAME_COLUMN = 4;
    private static final int EXAM_COLUMN = 6;
    private static final int CREDIT_COLUMN = 8;
    private static final int CREDIT_WITH_ASSESSMENT_COLUMN = 10;
    private static final int INTENSITY_COLUMN = 20;

    private static final int BOUND_ROW = 64;
    private static final Set<String> SKIP_ROW_NAMES = Set.of("-", "Наименование");

    public Map<String, DisciplinePlanInfo> getDisciplinePlanInfoMap() {
        Map<String, DisciplinePlanInfo> result = new HashMap<>();
        Sheet sheet = excelFile.getSheet(PLAN);
        if (sheet == null) {
            log.warn("sheet is null");
            throw new NullPointerException("sheet is null");
        }
        try {
            for (Row row : sheet) {
                if (row.getRowNum() >= BOUND_ROW) {
                    break;
                }
                Cell nameCell = row.getCell(DISCIPLINE_NAME_COLUMN);
                String disciplineName = nameCell.getStringCellValue();
                if (nameCell.getCellType() != CellType.STRING || SKIP_ROW_NAMES.contains(disciplineName)) {
                    continue;
                }
                List<AttestationType> attestationTypeList = new ArrayList<>();
                if (row.getCell(EXAM_COLUMN).getCellType() == CellType.STRING) {
                    attestationTypeList.add(AttestationType.EXAM);
                }
                if (row.getCell(CREDIT_COLUMN).getCellType() == CellType.STRING) {
                    attestationTypeList.add(AttestationType.CREDIT);
                }
                if (row.getCell(CREDIT_WITH_ASSESSMENT_COLUMN).getCellType() == CellType.STRING) {
                    attestationTypeList.add(AttestationType.CREDIT_WITH_ASSESSMENT);
                }
                var cell = row.getCell(INTENSITY_COLUMN);

                var value = cell.getStringCellValue();
                Integer intensity;
                if (value != null && value.isBlank()) {
                    intensity = null;
                } else {
                    assert value != null;
                    intensity = Integer.parseInt(value);
                }
                result.put(disciplineName, new DisciplinePlanInfo(intensity, attestationTypeList));
            }
        } catch (NullPointerException e) {
            log.warn(String.valueOf(result.size()));
            throw new RuntimeException(e);
        }
        return result;
    }
}
