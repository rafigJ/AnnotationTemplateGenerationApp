package ru.vsu.cs.dzhabbarov.scanner;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CompetitionPair {

    private String index;

    @EqualsAndHashCode.Exclude
    private String content;
}
