package core.basesyntax.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FruitTransactionOperationTest {

    @Test
    void getByCode_validBalanceCode_returnsBalanceOperation() {
        FruitTransaction.Operation operation = FruitTransaction.Operation.getByCode("b");
        assertNotNull(operation);
        assertEquals(FruitTransaction.Operation.BALANCE, operation);
    }

    @Test
    void getByCode_validSupplyCode_returnsSupplyOperation() {
        FruitTransaction.Operation operation = FruitTransaction.Operation.getByCode("s");
        assertNotNull(operation);
        assertEquals(FruitTransaction.Operation.SUPPLY, operation);
    }

    @Test
    void getByCode_validPurchaseCode_returnsPurchaseOperation() {
        FruitTransaction.Operation operation = FruitTransaction.Operation.getByCode("p");
        assertNotNull(operation);
        assertEquals(FruitTransaction.Operation.PURCHASE, operation);
    }

    @Test
    void getByCode_validReturnCode_returnsReturnOperation() {
        FruitTransaction.Operation operation = FruitTransaction.Operation.getByCode("r");
        assertNotNull(operation);
        assertEquals(FruitTransaction.Operation.RETURN, operation);
    }

    @Test
    void getByCode_unknownCode_throwsIllegalArgumentException() {
        String unknownCode = "x";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> FruitTransaction.Operation.getByCode(unknownCode));
        assertEquals("Unknown operation code: " + unknownCode, exception.getMessage());
    }

    @Test
    void getByCode_nullCode_throwsIllegalArgumentException() {
        String nullCode = null;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> FruitTransaction.Operation.getByCode(nullCode));
        assertEquals("Code cannot be null or empty", exception.getMessage());
    }

    @Test
    void getByCode_emptyCode_throwsIllegalArgumentException() {
        String emptyCode = "";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> FruitTransaction.Operation.getByCode(emptyCode));
        assertEquals("Code cannot be null or empty", exception.getMessage());
    }
}
