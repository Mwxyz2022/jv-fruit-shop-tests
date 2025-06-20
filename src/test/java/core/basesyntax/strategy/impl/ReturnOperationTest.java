package core.basesyntax.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.model.FruitTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnOperationTest {
    private FruitDao fruitDao;
    private ReturnOperation returnOperation;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
        returnOperation = new ReturnOperation(fruitDao);
    }

    @Test
    void handle_newFruitReturn_addsToZeroQuantity() {
        FruitTransaction transaction = new FruitTransaction("r", "strawberry", 15);
        returnOperation.handle(transaction);
        assertEquals(15, fruitDao.get("strawberry"));
    }

    @Test
    void handle_existingFruitReturn_addsToExistingQuantity() {
        fruitDao.update("watermelon", 150);
        FruitTransaction transaction = new FruitTransaction("r", "watermelon", 50);
        returnOperation.handle(transaction);
        assertEquals(200, fruitDao.get("watermelon"));
    }

    @Test
    void handle_zeroReturnQuantity_doesNotChangeQuantity() {
        fruitDao.update("blueberry", 80);
        FruitTransaction transaction = new FruitTransaction("r", "blueberry", 0);
        returnOperation.handle(transaction);
        assertEquals(80, fruitDao.get("blueberry"));
    }

    @Test
    void handle_nullFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("r", null, 10);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class, () -> returnOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_emptyFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("r", "", 10);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class, () -> returnOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_returnWithNegativeResultingQuantity_throwsIllegalArgumentException() {
        String fruitName = "lime";
        fruitDao.update(fruitName, 5);
        FruitTransaction transaction = new FruitTransaction("r", fruitName, -10);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class, () -> returnOperation.handle(transaction));

        assertEquals(
                "Quantity cannot be negative for fruit: " + fruitName, exception.getMessage());
    }

    @Test
    void handle_returnNegativeQuantityButResultIsPositive_updatesQuantity() {
        String fruitName = "coconut";
        fruitDao.update(fruitName, 20);
        FruitTransaction transaction = new FruitTransaction("r", fruitName, -5);

        returnOperation.handle(transaction);
        assertEquals(15, fruitDao.get(fruitName));
    }
}
