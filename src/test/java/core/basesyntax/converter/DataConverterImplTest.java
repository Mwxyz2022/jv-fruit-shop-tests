package core.basesyntax.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.basesyntax.model.FruitTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataConverterImplTest {
    private DataConverterImpl dataConverter;

    @BeforeEach
    void setUp() {
        dataConverter = new DataConverterImpl();
    }

    @Test
    void convertToTransaction_validInput_returnsCorrectTransactions() {
        List<String> input = List.of(
                "type,fruit,quantity",
                "b,banana,100",
                "s,apple,50",
                "p,banana,20",
                "r,apple,10"
        );

        List<FruitTransaction> expected = List.of(
                new FruitTransaction("b", "banana", 100),
                new FruitTransaction("s", "apple", 50),
                new FruitTransaction("p", "banana", 20),
                new FruitTransaction("r", "apple", 10)
        );

        List<FruitTransaction> actual = dataConverter.convertToTransaction(input);
        assertEquals(expected, actual);
    }

    @Test
    void convertToTransaction_singleValidLineAfterHeader_returnsOneTransaction() {
        List<String> input = List.of(
                "type,fruit,quantity",
                "b,grape,25"
        );
        List<FruitTransaction> transactions = dataConverter.convertToTransaction(input);
        assertEquals(1, transactions.size());
        assertEquals("grape", transactions.get(0).getFruit());
        assertEquals(25, transactions.get(0).getQuantity());
    }

    @Test
    void convertToTransaction_onlyHeader_returnsEmptyList() {
        List<String> input = Collections.singletonList("type,fruit,quantity");
        List<FruitTransaction> transactions = dataConverter.convertToTransaction(input);
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void convertToTransaction_invalidLineFormat_throwsIllegalArgumentException() {
        List<String> input = List.of(
                "type,fruit,quantity",
                "b,banana",
                "s,apple,50"
        );
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> dataConverter.convertToTransaction(input));
        assertEquals("Invalid data format in line: b,banana", exception.getMessage());
    }

    @Test
    void convertToTransaction_invalidQuantityFormat_throwsRuntimeException() {
        List<String> input = List.of(
                "type,fruit,quantity",
                "b,banana,abc",
                "s,apple,50"
        );
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dataConverter.convertToTransaction(input));
        assertEquals("Invalid quantity format in line: b,banana,abc", exception.getMessage());
        assertInstanceOf(NumberFormatException.class, exception.getCause());
    }

    @Test
    void convertToTransaction_unknownOperationCode_throwsIllegalArgumentException() {
        List<String> input = List.of(
                "type,fruit,quantity",
                "x,kiwi,10"
        );

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> dataConverter.convertToTransaction(input));
        assertEquals("Unknown operation code: x", exception.getMessage());
    }

    @Test
    void convertToTransaction_nullInputReport_throwsIllegalArgumentException() {
        List<String> input = null;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> dataConverter.convertToTransaction(input));
        assertEquals("Input report cannot be null or empty", exception.getMessage());
    }

    @Test
    void convertToTransaction_emptyInputReport_throwsIllegalArgumentException() {
        List<String> input = new ArrayList<>();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> dataConverter.convertToTransaction(input));
        assertEquals("Input report cannot be null or empty", exception.getMessage());
    }
}
