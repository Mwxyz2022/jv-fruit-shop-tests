package core.basesyntax.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.model.FruitTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOperationTest {
    private FruitDao fruitDao;
    private PurchaseOperation purchaseOperation;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
        purchaseOperation = new PurchaseOperation(fruitDao);
    }

    @AfterEach
    void tearDown() {
        fruitDao.clear();
    }

    @Test
    void handle_sufficientFruitPurchase_deductsQuantity() {
        fruitDao.update("pear", 100);
        FruitTransaction transaction = new FruitTransaction("p", "pear", 30);
        purchaseOperation.handle(transaction);
        assertEquals(70, fruitDao.get("pear"));
    }

    @Test
    void handle_exactFruitPurchase_quantityBecomesZero() {
        fruitDao.update("kiwi", 50);
        FruitTransaction transaction = new FruitTransaction("p", "kiwi", 50);
        purchaseOperation.handle(transaction);
        assertEquals(0, fruitDao.get("kiwi"));
    }

    @Test
    void handle_notEnoughFruitPurchase_throwsRuntimeException() {
        fruitDao.update("cherry", 10);
        FruitTransaction transaction = new FruitTransaction("p", "cherry", 20);

        Exception exception = assertThrows(RuntimeException.class,
                () -> purchaseOperation.handle(transaction));
        assertEquals(
                "Not enough fruit in storage to perform purchase. "
                        + "Available: 10, but tried to purchase: 20 of cherry",
                exception.getMessage());
        assertEquals(10, fruitDao.get("cherry"));
    }

    @Test
    void handle_purchaseWhenZeroFruit_throwsRuntimeException() {
        fruitDao.update("lime", 0);
        FruitTransaction transaction = new FruitTransaction("p", "lime", 5);

        Exception exception = assertThrows(RuntimeException.class,
                () -> purchaseOperation.handle(transaction));
        assertEquals(
                "Not enough fruit in storage to perform purchase. "
                        + "Available: 0, but tried to purchase: 5 of lime",
                exception.getMessage());
        assertEquals(0, fruitDao.get("lime"));
    }

    @Test
    void handle_zeroPurchaseQuantity_doesNotChangeQuantity() {
        fruitDao.update("plum", 40);
        FruitTransaction transaction = new FruitTransaction("p", "plum", 0);
        purchaseOperation.handle(transaction);
        assertEquals(40, fruitDao.get("plum"));
    }

    @Test
    void handle_nullFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("p", null, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_emptyFruitNameInTransaction_throwsIllegalArgumentException() {
        FruitTransaction transaction = new FruitTransaction("p", "", 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOperation.handle(transaction));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void handle_negativePurchaseQuantity_increasesStock() {
        String fruitName = "apricot";
        fruitDao.update(fruitName, 20);
        FruitTransaction transaction = new FruitTransaction("p", fruitName, -10);
        purchaseOperation.handle(transaction);
        assertEquals(30, fruitDao.get(fruitName));
    }

    @Test
    void handle_purchaseNonExistingFruit_throwsRuntimeException() {
        String fruitName = "nonExistingFruit";
        FruitTransaction transaction = new FruitTransaction("p", fruitName, 5);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseOperation.handle(transaction));
        assertEquals(
                "Not enough fruit in storage to perform purchase. "
                        + "Available: 0, but tried to purchase: 5 of "
                        + fruitName,
                exception.getMessage());
        assertEquals(0, fruitDao.get(fruitName));
    }
}
