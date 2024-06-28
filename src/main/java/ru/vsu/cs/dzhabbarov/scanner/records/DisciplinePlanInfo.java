package ru.vsu.cs.dzhabbarov.scanner.records;

import ru.vsu.cs.dzhabbarov.scanner.enums.AttestationType;

import java.util.List;

public record DisciplinePlanInfo(int intensity, List<AttestationType> attestationTypeList) {
}
