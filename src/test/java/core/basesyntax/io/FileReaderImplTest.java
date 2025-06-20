package core.basesyntax.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileReaderImplTest {
    private FileReader fileReader;
    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        fileReader = new FileReaderImpl();
    }

    @Test
    void read_existingFileWithContent_returnsCorrectLines() throws IOException {
        Path testFile = tempDir.resolve("test_input.txt");
        List<String> content = List.of("line1", "line2", "line3");
        Files.write(testFile, content);

        List<String> result = fileReader.read(testFile.toString());
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.size(), "Number of lines does not match");
        assertEquals(content, result, "File content does not match expected");
    }

    @Test
    void read_emptyFile_returnsEmptyList() throws IOException {
        Path testFile = tempDir.resolve("empty_input.txt");
        Files.write(testFile, Collections.emptyList());

        List<String> result = fileReader.read(testFile.toString());
        assertNotNull(result, "Result should not be null for an empty file");
        assertTrue(result.isEmpty(), "List should be empty for an empty file");
    }

    @Test
    void read_nonExistentFile_throwsRuntimeException() {
        String nonExistentFilePath = tempDir.resolve("non_existent_file.txt").toString();
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileReader.read(nonExistentFilePath));
        assertTrue(exception.getMessage()
                        .contains("Can't read data from file: " + nonExistentFilePath),
                "Error message does not contain expected text");
        assertInstanceOf(IOException.class, exception.getCause(),
                "The cause of the exception should be IOException");
    }

    @Test
    void read_nullFilePath_throwsIllegalArgumentException() {
        String nullFilePath = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileReader.read(nullFilePath));
        assertEquals("File path can't be null or empty", exception.getMessage(),
                "Error message for null path does not match");
    }

    @Test
    void read_emptyFilePath_throwsIllegalArgumentException() {
        String emptyFilePath = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileReader.read(emptyFilePath));
        assertEquals("File path can't be null or empty", exception.getMessage(),
                "Error message for empty path does not match");
    }
}
