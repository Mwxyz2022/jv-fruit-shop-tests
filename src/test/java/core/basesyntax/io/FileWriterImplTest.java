package core.basesyntax.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileWriterImplTest {
    @TempDir
    private Path tempDir;

    private FileWriter fileWriter;

    @BeforeEach
    void setUp() {
        fileWriter = new FileWriterImpl();
    }

    @Test
    void write_validReportAndPath_createsFileWithContent() throws IOException {
        String reportContent = "fruit,quantity\napple,100\nbanana,50";
        Path outputFile = tempDir.resolve("test_output.csv");
        String filePath = outputFile.toString();

        fileWriter.write(reportContent, filePath);

        assertTrue(Files.exists(outputFile));
        assertEquals(reportContent, Files.readString(outputFile));
    }

    @Test
    void write_emptyReport_createsEmptyFile() throws IOException {
        String reportContent = "";
        Path outputFile = tempDir.resolve("empty_output.csv");
        String filePath = outputFile.toString();

        fileWriter.write(reportContent, filePath);

        assertTrue(Files.exists(outputFile));
        assertTrue(Files.readString(outputFile).isEmpty());
    }

    @Test
    void write_nullReportContent_throwsNullPointerException() {
        String nullReportContent = null;
        Path outputFile = tempDir.resolve("null_report.txt");
        String filePath = outputFile.toString();

        assertThrows(NullPointerException.class,
                () -> fileWriter.write(nullReportContent, filePath));

        assertTrue(Files.notExists(outputFile));
    }

    @Test
    void write_invalidFilePath_throwsRuntimeException() {
        String reportContent = "some content";
        String invalidFilePath = "/non/existent/path/for/sure/file.txt";

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> fileWriter.write(reportContent, invalidFilePath));

        assertTrue(exception.getMessage()
                .contains("Can't write data to file: " + invalidFilePath));
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void write_nullFilePath_throwsIllegalArgumentException() {
        String reportContent = "some content";
        String nullFilePath = null;

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> fileWriter.write(reportContent, nullFilePath));

        assertEquals("File path can't be null or empty", exception.getMessage());
    }

    @Test
    void write_emptyFilePath_throwsIllegalArgumentException() {
        String reportContent = "some content";
        String emptyFilePath = "";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> fileWriter.write(reportContent, emptyFilePath));

        assertEquals("File path can't be null or empty", exception.getMessage());
    }
}
