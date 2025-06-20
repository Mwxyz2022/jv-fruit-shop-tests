package core.basesyntax.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.model.FruitTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BalanceOperationTest {
    private FruitDao fruitDao;
    private BalanceOperation balanceOperation;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
        balanceOperation = new BalanceOperation(fruitDao);
    }

    @Test
    void handle_newFruitBalance_setsCorrectQuantity() {
        FruitTransaction transaction = new FruitTransaction("b", "apple", 100);
        balanceOperation.handle(transaction);
        assertEquals(100, fruitDao.get("apple"));
    }

    @Test
    void handle_existingFruitBalance_overwritesQuantity() {
        fruitDao.update("banana", 50);

        FruitTransaction transaction = new FruitTransaction("b", "banana", 120);
        balanceOperation.handle(transaction);
        assertEquals(120, fruitDao.get("banana"));
    }

    @Test
    void handle_zeroBalance_setsZeroQuantity() {
        fruitDao.update("orange", 75);

        FruitTransaction transaction = new FruitTransaction("b", "orange", 0);
        balanceOperation.handle(transaction);
        assertEquals(0, fruitDao.get("orange"));
    }

    @Test
    void handle_nullFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("b", null, 10);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class, () -> balanceOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_emptyFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("b", "", 10);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class, () -> balanceOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_negativeQuantityInTransaction_throwsIllegalArgumentException() {
        String fruitName = "kiwi";
        FruitTransaction transaction = new FruitTransaction("b", fruitName, -5);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class, () -> balanceOperation.handle(transaction));
        assertEquals(
                "Quantity cannot be negative for fruit: " + fruitName, exception.getMessage());
    }
}
