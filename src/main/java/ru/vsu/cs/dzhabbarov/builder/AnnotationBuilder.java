package ru.vsu.cs.dzhabbarov.builder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import ru.vsu.cs.dzhabbarov.scanner.CompetitionPair;
import ru.vsu.cs.dzhabbarov.scanner.records.Discipline;
import ru.vsu.cs.dzhabbarov.scanner.records.DisciplinePlanInfo;
import ru.vsu.cs.dzhabbarov.scanner.DisciplineScanner;
import ru.vsu.cs.dzhabbarov.scanner.PlanScanner;

import java.util.List;
import java.util.Map;

import static ru.vsu.cs.dzhabbarov.AnnotationConst.*;

@Slf4j
@RequiredArgsConstructor
public class AnnotationBuilder {

    private final Workbook excelFile;

    public void build() {
        List<Discipline> disciplineList = new DisciplineScanner(excelFile).getDisciplineList();
        Map<String, DisciplinePlanInfo> disciplonePlanMap = new PlanScanner(excelFile).getDisciplinePlanInfoMap();


        log.info(" ".repeat(10) + HEADER);

        for (Discipline discipline : disciplineList) {
            log.info(" ".repeat(4) + discipline.index() + " " + discipline.name());
            DisciplinePlanInfo info = disciplonePlanMap.get(discipline.name());
            log.info("\n\n" + " ".repeat(6) + TOTAL_LABOR_INTENSITY + info.intensity() + INTENSITY_METRIC + "\n\n");
            log.info(DISCIPLINE_AIM_TO);
            var competitionPairListMap = discipline.competitionPairListMap();
            for (Map.Entry<CompetitionPair, List<CompetitionPair>> entry : competitionPairListMap.entrySet()) {
                var rootPair = entry.getKey();
                log.info(" ".repeat(8) + "%s. %s".formatted(rootPair.getIndex(), rootPair.getContent()));
                for (CompetitionPair subPair : entry.getValue()) {
                    log.info(" ".repeat(10) + "- %s. %s".formatted(subPair.getIndex(), subPair.getContent()));
                }
            }

            log.info("\n\n" + " ".repeat(6) + PLACE_ACADEMIC_DISCIPLINE + ACADEMIC_DISCIPLINE_REFER_TO + "???");

        }
    }

}
