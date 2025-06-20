package core.basesyntax.strategy.impl;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.model.FruitTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplyOperationTest {
    private FruitDao fruitDao;
    private SupplyOperation supplyOperation;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
        supplyOperation = new SupplyOperation(fruitDao);
    }

    @Test
    void handle_newFruitSupply_addsToZeroQuantity() {
        FruitTransaction transaction = new FruitTransaction("s", "mango", 40);
        supplyOperation.handle(transaction);
        assertEquals(40, fruitDao.get("mango"));
    }

    @Test
    void handle_existingFruitSupply_addsToExistingQuantity() {
        fruitDao.update("grape", 70);
        FruitTransaction transaction = new FruitTransaction("s", "grape", 30);
        supplyOperation.handle(transaction);
        assertEquals(100, fruitDao.get("grape"));
    }

    @Test
    void handle_zeroSupplyQuantity_doesNotChangeQuantity() {
        fruitDao.update("peach", 60);
        FruitTransaction transaction = new FruitTransaction("s", "peach", 0);
        supplyOperation.handle(transaction);
        assertEquals(60, fruitDao.get("peach"));
    }

    @Test
    void handle_nullFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("s", null, 10);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> supplyOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_emptyFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("s", "", 10);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> supplyOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_supplyWithNegativeResultingQuantity_throwsIllegalArgumentException() {
        String fruitName = "apple";
        fruitDao.update(fruitName, 10);
        FruitTransaction transaction = new FruitTransaction("s", fruitName, -15);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> supplyOperation.handle(transaction));
        assertEquals("Quantity cannot be negative for fruit: " + fruitName, exception.getMessage());
    }

    @Test
    void handle_supplyNegativeQuantityButResultIsPositive_updatesQuantity() {
        String fruitName = "banana";
        fruitDao.update(fruitName, 50);
        FruitTransaction transaction = new FruitTransaction("s", fruitName, -20);
        supplyOperation.handle(transaction);
        assertEquals(30, fruitDao.get(fruitName));
    }
}
