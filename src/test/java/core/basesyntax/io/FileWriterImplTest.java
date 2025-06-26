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
    private static final String VALID_REPORT_CONTENT = "fruit,quantity\napple,100\nbanana,50";
    private static final String EMPTY_REPORT_CONTENT = "";
    private static final String SOME_CONTENT = "some content";
    private static final String TEST_OUTPUT_FILE_NAME = "test_output.csv";
    private static final String EMPTY_OUTPUT_FILE_NAME = "empty_output.csv";
    private static final String NULL_REPORT_FILE_NAME = "null_report.csv";
    private static final String INVALID_FILE_PATH = "/non/existent/path/for/sure/file.txt";

    @TempDir
    private Path tempDir;

    private FileWriter fileWriter;

    @BeforeEach
    void setUp() {
        fileWriter = new FileWriterImpl();
    }

    @Test
    void write_validReportAndPath_createsFileWithContent() throws IOException {
        Path outputFile = tempDir.resolve(TEST_OUTPUT_FILE_NAME);
        String filePath = outputFile.toString();

        fileWriter.write(VALID_REPORT_CONTENT, filePath);

        assertTrue(Files.exists(outputFile));
        assertEquals(VALID_REPORT_CONTENT, Files.readString(outputFile));
    }

    @Test
    void write_emptyReport_createsEmptyFile() throws IOException {
        Path outputFile = tempDir.resolve(EMPTY_OUTPUT_FILE_NAME);
        String filePath = outputFile.toString();

        fileWriter.write(EMPTY_REPORT_CONTENT, filePath);

        assertTrue(Files.exists(outputFile));
        assertTrue(Files.readString(outputFile).isEmpty());
    }

    @Test
    void write_nullReportContent_throwsIllegalArgumentException() {
        String nullReportContent = null;
        Path outputFile = tempDir.resolve(NULL_REPORT_FILE_NAME);
        String filePath = outputFile.toString();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileWriter.write(nullReportContent, filePath));

        assertEquals("Report content can't be null", exception.getMessage());
        assertTrue(Files.notExists(outputFile));
    }

    @Test
    void write_invalidFilePath_throwsRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileWriter.write(SOME_CONTENT, INVALID_FILE_PATH));
        assertTrue(exception.getMessage()
                .contains("Can't write data to file: " + INVALID_FILE_PATH));
        assertInstanceOf(IOException.class, exception.getCause(),
                "The cause of the exception should be IOException");
    }

    @Test
    void write_nullFilePath_throwsIllegalArgumentException() {
        String nullFilePath = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileWriter.write(SOME_CONTENT, nullFilePath));
        assertEquals("File path can't be null or empty", exception.getMessage(),
                "Error message for null path does not match");
    }

    @Test
    void write_emptyFilePath_throwsIllegalArgumentException() {
        String emptyFilePath = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileWriter.write(SOME_CONTENT, emptyFilePath));
        assertEquals("File path can't be null or empty", exception.getMessage(),
                "Error message for empty path does not match");
    }
}
