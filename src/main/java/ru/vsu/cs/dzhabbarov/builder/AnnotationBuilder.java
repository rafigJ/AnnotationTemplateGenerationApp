package ru.vsu.cs.dzhabbarov.builder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import ru.vsu.cs.dzhabbarov.scanner.CompetitionPair;
import ru.vsu.cs.dzhabbarov.scanner.DisciplineScanner;
import ru.vsu.cs.dzhabbarov.scanner.GoalsScanner;
import ru.vsu.cs.dzhabbarov.scanner.PlanScanner;
import ru.vsu.cs.dzhabbarov.scanner.enums.AttestationType;
import ru.vsu.cs.dzhabbarov.scanner.records.Discipline;
import ru.vsu.cs.dzhabbarov.scanner.records.DisciplinePlanInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.vsu.cs.dzhabbarov.AnnotationConst.ACADEMIC_DISCIPLINE_REFER_TO;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.ATTESTATION_FORM;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.DISCIPLINE_AIM_TO;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.GOALS_AND_OBJECTIVES;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.HEADER;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.INTENSITY_METRIC;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.PLACE_ACADEMIC_DISCIPLINE;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.REFERS_TO_B1;
import static ru.vsu.cs.dzhabbarov.AnnotationConst.TOTAL_LABOR_INTENSITY;

@Slf4j
@RequiredArgsConstructor
public class AnnotationBuilder {

    private final Workbook excelFile;
    private final Workbook goalExcelFile;
    private final List<LogEntry> logEntries = new ArrayList<>();

    public void build(Path outputPath) {
        List<Discipline> disciplineList = new DisciplineScanner(excelFile).getDisciplineList();
        Map<String, DisciplinePlanInfo> disciplonePlanMap = new PlanScanner(excelFile).getDisciplinePlanInfoMap();
        Map<String, String> goals = new GoalsScanner(goalExcelFile).getIndexGoalsMap();

        logAndStore(ParagraphAlignment.CENTER, false, 0, boldText(HEADER));

        for (Discipline discipline : disciplineList) {
            logAndStore(ParagraphAlignment.CENTER, false, 0, boldText(discipline.index() + " " + discipline.name()));
            DisciplinePlanInfo info = disciplonePlanMap.get(discipline.name());
            String intensity = info.intensity() == null ? "x/328" : info.intensity() + INTENSITY_METRIC;
            logAndStore(ParagraphAlignment.LEFT, false, 0, text(TOTAL_LABOR_INTENSITY + intensity));
            logAndStore(ParagraphAlignment.LEFT, false, 0, italicText(DISCIPLINE_AIM_TO));
            var competitionPairListMap = discipline.competitionPairListMap();
            for (Map.Entry<CompetitionPair, List<CompetitionPair>> entry : competitionPairListMap.entrySet()) {
                var rootPair = entry.getKey();
                logAndStore(ParagraphAlignment.LEFT, false, 1, text("%s. %s".formatted(rootPair.getIndex(), rootPair.getContent())));
                for (CompetitionPair subPair : entry.getValue()) {
                    char dash = '-';
                    logAndStore(ParagraphAlignment.LEFT, true, 2, text("%s %s. %s".formatted(dash, subPair.getIndex(), subPair.getContent())));
                }
            }

            logAndStore(ParagraphAlignment.LEFT, false, 0, boldText(PLACE_ACADEMIC_DISCIPLINE), text(ACADEMIC_DISCIPLINE_REFER_TO + REFERS_TO_B1));
            logAndStore(ParagraphAlignment.LEFT, false, 0, boldText(GOALS_AND_OBJECTIVES), text(goals.getOrDefault(discipline.index(), "")));
            var collectedAttestationForm = info.attestationTypeList().stream().map(AttestationType::getTypeName).collect(Collectors.joining(", "));
            logAndStore(ParagraphAlignment.LEFT, false, 0, boldText(ATTESTATION_FORM), text(collectedAttestationForm + "."));
        }
        writeLogsToWordFile(outputPath);
    }

    private void logAndStore(ParagraphAlignment alignment, boolean listItem, int indentationLevel, StyledText... message) {
        log.info(Arrays.stream(message).map(m -> m.text).collect(Collectors.joining()));
        logEntries.add(new LogEntry(List.of(message), alignment, listItem, indentationLevel));
    }

    private void writeLogsToWordFile(Path filePath) {
        try (XWPFDocument document = new XWPFDocument()) {
            for (LogEntry entry : logEntries) {
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.setAlignment(entry.alignment());
                paragraph.setIndentationLeft(entry.indentationLevel() * 200); // Adjust indentation level as needed
                for (StyledText part : entry.messages()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(part.text());
                    run.setFontFamily("Calibri");
                    run.setFontSize(11);
                    run.setBold(part.bold());
                    run.setItalic(part.italic());
                }
            }
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                document.write(out);
            }
        } catch (IOException e) {
            log.error("Failed to write logs to Word file", e);
        }
    }

    private static StyledText boldText(String text) {
        return new StyledText(text, true, false);
    }

    private static StyledText text(String text) {
        return new StyledText(text, false, false);
    }

    private static StyledText italicText(String text) {
        return new StyledText(text, false, true);
    }

    private record LogEntry(List<StyledText> messages, ParagraphAlignment alignment, boolean listItem,
                            int indentationLevel) {
    }

    private record StyledText(String text, boolean bold, boolean italic) {
    }
}

