package ru.vsu.cs.dzhabbarov.scanner.records;

import ru.vsu.cs.dzhabbarov.scanner.enums.AttestationType;

import java.util.List;

public record DisciplinePlanInfo(Integer intensity, List<AttestationType> attestationTypeList) {
}
