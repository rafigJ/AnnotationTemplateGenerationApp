package ru.vsu.cs.dzhabbarov.scanner.enums;

import lombok.Getter;

@Getter
public enum AttestationType {

    EXAM("экзамен"),
    CREDIT("зачет"),
    CREDIT_WITH_ASSESSMENT("зачет с оценкой");

    private final String typeName;

    AttestationType(String typeName) {
        this.typeName = typeName;
    }

}
