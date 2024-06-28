package ru.vsu.cs.dzhabbarov.scanner.records;

import ru.vsu.cs.dzhabbarov.scanner.CompetitionPair;

import java.util.List;
import java.util.Map;

public record Discipline(String index, String name,
                         Map<CompetitionPair, List<CompetitionPair>> competitionPairListMap) {
}
