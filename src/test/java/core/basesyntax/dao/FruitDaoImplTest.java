package core.basesyntax.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FruitDaoImplTest {
    private FruitDaoImpl fruitDao;

    @BeforeEach
    void setUp() {
        fruitDao = new FruitDaoImpl();
    }

    @AfterEach
    void tearDown() {
        fruitDao.clear();
    }

    @Test
    void update_validFruitAndQuantity_success() {
        String fruitName = "banana";
        int quantity = 100;
        fruitDao.update(fruitName, quantity);
        assertEquals(quantity, fruitDao.get(fruitName));
    }

    @Test
    void update_existingFruit_quantityIsUpdated() {
        String fruitName = "apple";
        fruitDao.update(fruitName, 50);
        fruitDao.update(fruitName, 75);
        assertEquals(75, fruitDao.get(fruitName));
    }

    @Test
    void update_zeroQuantity_success() {
        String fruitName = "orange";
        fruitDao.update(fruitName, 0);
        assertEquals(0, fruitDao.get(fruitName));
    }

    @Test
    void update_nullFruitName_throwsIllegalArgumentException() {
        String fruitName = null;
        int quantity = 10;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fruitDao.update(fruitName, quantity));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void update_emptyFruitName_throwsIllegalArgumentException() {
        String fruitName = "";
        int quantity = 10;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fruitDao.update(fruitName, quantity));
        assertEquals("Fruit name can't be null or empty", exception.getMessage());
    }

    @Test
    void update_negativeQuantity_throwsIllegalArgumentException() {
        String fruitName = "mango";
        int quantity = -5;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fruitDao.update(fruitName, quantity));
        assertEquals("Quantity cannot be negative for fruit: " + fruitName,
                exception.getMessage());
    }

    @Test
    void get_existingFruit_returnsCorrectQuantity() {
        String fruitName = "grape";
        int quantity = 200;
        fruitDao.update(fruitName, quantity);
        assertEquals(quantity, fruitDao.get(fruitName));
    }

    @Test
    void get_nonExistingFruit_returnsZero() {
        String fruitName = "pineapple";
        assertEquals(0, fruitDao.get(fruitName));
    }

    @Test
    void get_nullFruitName_returnsZero() {
        assertEquals(0, fruitDao.get(null));
    }

    @Test
    void get_emptyFruitName_returnsZero() {
        assertEquals(0, fruitDao.get(""));
    }

    @Test
    void getAll_emptyStorage_returnsEmptyMap() {
        Map<String, Integer> allFruits = fruitDao.getAll();
        assertNotNull(allFruits);
        assertTrue(allFruits.isEmpty());
    }

    @Test
    void getAll_withFruits_returnsCorrectMap() {
        fruitDao.update("apple", 10);
        fruitDao.update("banana", 20);
        Map<String, Integer> expectedMap = new HashMap<>();
        expectedMap.put("apple", 10);
        expectedMap.put("banana", 20);
        assertEquals(expectedMap, fruitDao.getAll());
    }

    @Test
    void getAll_modifyingReturnedMap_doesNotAffectOriginalStorage() {
        fruitDao.update("apple", 10);
        Map<String, Integer> returnedMap = fruitDao.getAll();
        returnedMap.put("orange", 30);
        assertEquals(1, fruitDao.getAll().size());
        assertEquals(10, fruitDao.get("apple"));
    }
}
