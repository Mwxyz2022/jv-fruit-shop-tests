package core.basesyntax.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportGeneratorImplTest {
    private static final String HEADER = "fruit,quantity";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private FruitDao fruitDao;
    private ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
        reportGenerator = new ReportGeneratorImpl(fruitDao);
    }

    private Map<String, Integer> parseReportToMap(final String reportContent) {
        return reportContent.lines()
                .skip(1)
                .filter(line -> !line.trim().isEmpty())
                .map(line -> {
                    final String[] parts = line.split(",");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Malformed report line: " + line);
                    }
                    try {
                        return Map.entry(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Invalid quantity format in line: " + line, e);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Test
    void getReport_emptyStorage_returnsHeaderOnly() {
        final String expectedReport = HEADER + LINE_SEPARATOR;
        final String actualReport = reportGenerator.getReport();

        assertEquals(expectedReport, actualReport,
                "Report for empty storage should contain only the header and a line separator.");
        final Map<String, Integer> parsedContentMap = parseReportToMap(actualReport);
        assertTrue(parsedContentMap.isEmpty(),
                "Parsed content from an empty storage report should be an empty map.");
    }

    @Test
    void getReport_singleFruit_returnsCorrectReport() {
        fruitDao.update("apple", 150);
        final String expectedReport = HEADER + LINE_SEPARATOR
                + "apple,150" + LINE_SEPARATOR;
        final String actualReport = reportGenerator.getReport();

        assertEquals(expectedReport, actualReport,
                "Report for a single fruit is not formatted as expected.");
        final Map<String, Integer> parsedContentMap = parseReportToMap(actualReport);
        assertEquals(1, parsedContentMap.size(),
                "Parsed map should contain one entry for a single fruit report.");
        assertEquals(150, parsedContentMap.get("apple"),
                "Quantity for 'apple' in parsed map is incorrect.");
    }

    @Test
    void getReport_multipleFruits_returnsCorrectReport() {
        fruitDao.update("banana", 200);
        fruitDao.update("orange", 50);
        fruitDao.update("apple", 100);

        final String actualReport = reportGenerator.getReport();

        assertTrue(actualReport.startsWith(HEADER + LINE_SEPARATOR),
                "Report should start with the header and a line separator.");
        assertTrue(actualReport.endsWith(LINE_SEPARATOR),
                "Report should end with a line separator when it contains data.");

        final Map<String, Integer> actualFruitsFromReport = parseReportToMap(actualReport);

        assertEquals(fruitDao.getAll(), actualFruitsFromReport,
                "Parsed report content does not match the data in DAO for multiple fruits.");
    }

    @Test
    void getReport_fruitWithZeroQuantity_isIncludedInReport() {
        fruitDao.update("grape", 70);
        fruitDao.update("kiwi", 0);
        fruitDao.update("peach", 30);

        final String actualReport = reportGenerator.getReport();

        assertTrue(actualReport.startsWith(HEADER + LINE_SEPARATOR),
                "Report should start with the header and a line separator.");
        assertTrue(actualReport.endsWith(LINE_SEPARATOR),
                "Report should end with a line separator when it contains data.");

        final Map<String, Integer> actualFruitsFromReport = parseReportToMap(actualReport);

        assertEquals(fruitDao.getAll(), actualFruitsFromReport,
                "Parsed report content does not match DAO data when a fruit has zero quantity.");
        assertTrue(actualFruitsFromReport.containsKey("kiwi"),
                "Report should include 'kiwi' even with zero quantity.");
        assertEquals(0, actualFruitsFromReport.get("kiwi"),
                "Quantity for 'kiwi' should be 0 in the report.");
    }
}
