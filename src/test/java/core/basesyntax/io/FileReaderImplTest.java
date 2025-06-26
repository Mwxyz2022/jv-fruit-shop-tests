package core.basesyntax.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileReaderImplTest {
    private static final String EXISTING_FILE_NAME = "test_input.csv";
    private static final String EMPTY_FILE_NAME = "empty_input.csv";

    private FileReader fileReader;

    @BeforeEach
    void setUp() {
        fileReader = new FileReaderImpl();
    }

    @Test
    void read_existingFileWithContent_returnsCorrectLines() throws IOException {
        String filePath = getPathFromResources(EXISTING_FILE_NAME);
        List<String> expectedContent = List.of("line1", "line2", "line3");

        List<String> result = fileReader.read(filePath);
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedContent, result, "File content does not match expected");

    }

    @Test
    void read_emptyFile_returnsEmptyList() throws IOException {
        String filePath = getPathFromResources(EMPTY_FILE_NAME);

        List<String> result = fileReader.read(filePath);
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty");

    }

    @Test
    void read_nonExistentFile_throwsRuntimeException() {
        String nonExistentFilePath = Paths.get("path", "to", "non", "existent", "file.csv")
                .toString();
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

    private String getPathFromResources(String fileName) {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource(fileName);
            assertNotNull(resourceUrl, "Test file not found in resources: " + fileName
                    + ". Make sure the file exists in `src/test/resources`.");
            return Paths.get(resourceUrl.toURI()).toString();
        } catch (URISyntaxException e) {
            fail("Error getting path for resource: " + fileName, e);
            return null;
        }
    }
}
