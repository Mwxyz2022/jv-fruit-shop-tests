package core.basesyntax.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportGeneratorImplTest {
    private static final String HEADER = "fruit,quantity";

    private FruitDao fruitDao;
    private ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
        reportGenerator = new ReportGeneratorImpl(fruitDao);
    }

    @AfterEach
    void tearDown() {
        fruitDao.clear();
    }

    @Test
    void getReport_emptyStorage_returnsHeaderOnly() {
        String expectedReport = HEADER + System.lineSeparator();
        String actualReport = reportGenerator.getReport();
        assertEquals(expectedReport, actualReport,
                "Report for an empty storage should contain only the header.");
    }

    @Test
    void getReport_singleFruit_returnsCorrectReport() {
        fruitDao.update("apple", 150);
        String expectedReport = HEADER + System.lineSeparator()
                + "apple,150" + System.lineSeparator();
        String actualReport = reportGenerator.getReport();
        assertEquals(expectedReport, actualReport,
                "Report for a single fruit is formatted incorrectly.");
    }

    @Test
    void getReport_multipleFruits_returnsCorrectReport() {
        fruitDao.update("banana", 200);
        fruitDao.update("apple", 100);

        String actualReport = reportGenerator.getReport();

        assertTrue(actualReport.startsWith(HEADER + System.lineSeparator()),
                "Report should start with the header.");
        assertTrue(actualReport.contains("banana,200"),
                "Report should contain data for 'banana'.");
        assertTrue(actualReport.contains("apple,100"),
                "Report should contain data for 'apple'.");
        assertEquals(3, actualReport.lines().count(),
                "Report should contain 3 lines.");
        assertTrue(actualReport.endsWith(System.lineSeparator()),
                "Report should end with a new line character.");
    }

    @Test
    void getReport_fruitWithZeroQuantity_isIncludedInReport() {
        fruitDao.update("kiwi", 0);
        fruitDao.update("peach", 30);

        String actualReport = reportGenerator.getReport();

        assertTrue(actualReport.contains("kiwi,0"),
                "Report should include 'kiwi' with zero quantity.");
        assertTrue(actualReport.contains("peach,30"),
                "Report should contain data for 'peach'.");
        assertEquals(3, actualReport.lines().count(),
                "Report should contain 3 lines.");
    }
}
