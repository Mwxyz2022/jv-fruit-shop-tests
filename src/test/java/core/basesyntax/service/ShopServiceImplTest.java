package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.model.FruitTransaction;
import core.basesyntax.strategy.OperationHandler;
import core.basesyntax.strategy.OperationStrategy;
import core.basesyntax.strategy.OperationStrategyImpl;
import core.basesyntax.strategy.impl.BalanceOperation;
import core.basesyntax.strategy.impl.PurchaseOperation;
import core.basesyntax.strategy.impl.ReturnOperation;
import core.basesyntax.strategy.impl.SupplyOperation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShopServiceImplTest {
    private FruitDaoImpl fruitDao;
    private ShopServiceImpl shopService;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();

        final Map<FruitTransaction.Operation, OperationHandler> operationHandlers = new HashMap<>();
        operationHandlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperation(fruitDao));
        operationHandlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperation(fruitDao));
        operationHandlers.put(FruitTransaction.Operation.PURCHASE, new PurchaseOperation(fruitDao));
        operationHandlers.put(FruitTransaction.Operation.RETURN, new ReturnOperation(fruitDao));

        final OperationStrategy operationStrategy = new OperationStrategyImpl(operationHandlers);
        shopService = new ShopServiceImpl(operationStrategy);
    }

    @AfterEach
    void tearDown() {
        fruitDao.clear();
    }

    @Test
    void process_validTransactions_appliesOperationsCorrectly() {
        final List<FruitTransaction> transactions = List.of(
                new FruitTransaction("b", "apple", 100),
                new FruitTransaction("s", "banana", 50),
                new FruitTransaction("p", "apple", 30),
                new FruitTransaction("r", "banana", 10),
                new FruitTransaction("s", "apple", 20),
                new FruitTransaction("p", "banana", 20)
        );

        shopService.process(transactions);

        assertEquals(90, fruitDao.get("apple"));
        assertEquals(40, fruitDao.get("banana"));
        assertEquals(0, fruitDao.get("orange"));
    }

    @Test
    void process_emptyTransactionsList_storageRemainsUnchanged() {
        fruitDao.update("grape", 50);

        final List<FruitTransaction> transactions = Collections.emptyList();
        shopService.process(transactions);

        assertEquals(50, fruitDao.get("grape"));
        assertEquals(1, fruitDao.getAll().size());
    }

    @Test
    void process_nullTransactionsList_throwsIllegalArgumentException() {
        final List<FruitTransaction> transactions = null;
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> shopService.process(transactions));
        assertEquals("Transactions list cannot be null.", exception.getMessage());
    }

    @Test
    void process_purchaseNotEnoughFruit_throwsRuntimeExceptionAndStopsProcessing() {
        final List<FruitTransaction> transactions = List.of(
                new FruitTransaction("b", "orange", 10),
                new FruitTransaction("p", "orange", 20),
                new FruitTransaction("s", "apple", 5)
        );

        final RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> shopService.process(transactions));
        assertEquals(
                "Not enough fruit in storage to perform purchase. Available: 10, "
                        + "but tried to purchase: 20 of orange",
                exception.getMessage());

        assertEquals(10, fruitDao.get("orange"));
        assertEquals(0, fruitDao.get("apple"));
    }

    @Test
    void process_noHandlerFoundForOperation_throwsRuntimeExceptionAndStopsProcessing() {
        final Map<FruitTransaction.Operation, OperationHandler> customHandlers = new HashMap<>();
        customHandlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperation(fruitDao));
        customHandlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperation(fruitDao));

        final OperationStrategy customStrategy = new OperationStrategyImpl(customHandlers);
        final ShopServiceImpl customShopService = new ShopServiceImpl(customStrategy);
        final List<FruitTransaction> transactions = List.of(
                new FruitTransaction("b", "apple", 100),
                new FruitTransaction("p", "apple", 10),
                new FruitTransaction("s", "banana", 20)
        );

        final RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> customShopService.process(transactions));
        assertEquals("No handler found for operation: PURCHASE", exception.getMessage());
        assertEquals(100, fruitDao.get("apple"));
        assertEquals(0, fruitDao.get("banana"));
    }

    @Test
    void process_exceptionDuringNonPurchaseOperation_stopsProcessingAndThrows() {
        final List<FruitTransaction> transactions = List.of(
                new FruitTransaction("s", "banana", 50),
                new FruitTransaction("b", "apple", -10),
                new FruitTransaction("p", "banana", 20)
        );

        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> shopService.process(transactions));

        assertEquals("Quantity cannot be negative for fruit: apple", exception.getMessage());
        assertEquals(50, fruitDao.get("banana"));
        assertEquals(0, fruitDao.get("apple"));
        assertEquals(1, fruitDao.getAll().size());
    }
}
