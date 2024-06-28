package ru.vsu.cs.dzhabbarov.scanner;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DisciplineScanner {

    private static final Set<String> CHOOSE_DISCIPLINE_INDEXES = Set.of("Б1.В.ДВ.01", "Б1.В.ДВ.02", "Б1.В.ДВ.03", "Б1.В.ДВ.04", "Б1.В.ДВ.05", "Б1.В.ДВ.06");
    private static final Set<String> SKIP_DISCIPLINE_INDEXES = Set.of("Б2.О.01(У)", "Б2.О.02(Н)", "Б2.В.01(П)", "Б2.В.02(Н)", "Б3.01(Д)");

    private static final String COMPETITION = "Компетенции(2)";
    private static final String INDEX = "Индекс";
    private static final int ROOT_INDEX_COLUMN = 0;
    private static final int SUB_ROOT_INDEX_COLUMN = 1;
    private static final int INDEX_COLUMN = 2;
    private static final int DISCIPLINE_NAME_COLUMN = 5;
    private static final int COMPETITIONS_COLUMN = 6;

    private final Workbook excelFile;
    private final Map<CompetitionPair, List<CompetitionPair>> competitionPairListMap;
    private final Map<String, CompetitionPair> rootCompetitionNameCompetition;

    public DisciplineScanner(Workbook excelFile) {
        this.excelFile = excelFile;
        this.competitionPairListMap = new CompetitionScanner(excelFile).getCompetitionMap();
        rootCompetitionNameCompetition = new HashMap<>();
        for (CompetitionPair competitionPair : competitionPairListMap.keySet()) {
            rootCompetitionNameCompetition.put(competitionPair.getIndex(), competitionPair);
        }
    }

    public List<Discipline> getDisciplineList() {

        List<Discipline> result = new ArrayList<>();
        Sheet sheet = excelFile.getSheet(COMPETITION);
        if (sheet == null) {
            log.warn("sheet is null");
            throw new NullPointerException("sheet is null");
        }

        try {
            boolean isChosenDiscipline = false;
            for (Row row : sheet) {
                // Не обрабатываем случаи Б1 (rootIndex), Б1.О (subRootIndex)
                Cell rootCell = row.getCell(ROOT_INDEX_COLUMN);
                Cell subRootCell = row.getCell(SUB_ROOT_INDEX_COLUMN);
                if (rootCell.getCellType() == CellType.STRING || subRootCell.getCellType() == CellType.STRING) {
                    continue;
                }

                Cell indexCell = row.getCell(isChosenDiscipline ? INDEX_COLUMN + 1 : INDEX_COLUMN);
                Cell nameCell = row.getCell(DISCIPLINE_NAME_COLUMN);
                Cell competitionsCell = row.getCell(COMPETITIONS_COLUMN);
                if (indexCell.getCellType() != CellType.STRING) {
                    isChosenDiscipline = false;
                    continue; // не обрабатываем если он пуст
                }
                String indexValue = indexCell.getStringCellValue();
                String competitionsValue = competitionsCell.getStringCellValue();

                if (CHOOSE_DISCIPLINE_INDEXES.contains(indexValue)) {
                    isChosenDiscipline = true;
                    continue;
                }
                if (SKIP_DISCIPLINE_INDEXES.contains(indexValue) || competitionsValue.isBlank()) {
                    continue;
                }
                String nameValue = nameCell.getStringCellValue();
                Map<CompetitionPair, List<CompetitionPair>> competitions = new LinkedHashMap<>();

                for (String compName : competitionsValue.split("; ")) {
                    CompetitionPair root = rootCompetitionNameCompetition.get(compName.split("\\.")[0]);
                    CompetitionPair pair = competitionPairListMap.get(root).parallelStream()
                            .filter(competitionPair -> competitionPair.getIndex().equals(compName))
                            .findAny().orElseThrow();
                    competitions.putIfAbsent(root, new ArrayList<>());
                    competitions.get(root).add(pair);
                }
                result.add(new Discipline(indexValue, nameValue, competitions));
            }
            return result;
        } catch (NullPointerException e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public record Discipline(String index, String disciplineName,
                             Map<CompetitionPair, List<CompetitionPair>> competitionPairListMap) {
    }
}
