package collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FmMapTest {

    private static FmMap<String, Integer> generateMap() {
        return new FmMap<String, Integer>().set("Bob", 15).set("Harry", 19).set("Mark", 24);
    }

    private static FmMap<String, Integer> generateAltMap() {
        return new FmMap<String, Integer>().set("Bob", 15).set("Julia", 19).set("Mark", 35);
    }

    @Test
    void random() {
        var result = generateMap().random();
        assertTrue(result == 15 || result == 19 || result == 24);
    }

    @Test
    void randomKey() {
        var result = generateMap().randomKey();
        assertTrue(result.equals("Bob") || result.equals("Harry") || result.equals("Mark"));
    }

    @Test
    void map() {
        var result = generateMap().map((k, v) -> k + " is " + v);
        assertEquals("Bob is 15", result.get(0));
        assertEquals("Harry is 19", result.get(1));
        assertEquals("Mark is 24", result.get(2));
    }

    @Test
    void filter() {
        var result = generateMap().filter((k, v) -> k.equals("Bob") || v == 24);
        assertEquals(2, result.size());
        assertEquals(24, result.get("Mark"));
        assertEquals(15, result.get("Bob"));
        assertNull(result.get("Harry"));
    }

    @Test
    void sweep() {
        var result = generateMap().sweep((k, v) -> k.equals("Bob") || v == 24);
        assertEquals(1, result.size());
        assertEquals(19, result.get("Harry"));
        assertNull(result.get("Bob"));
        assertNull(result.get("Mark"));
    }

    @Test
    void everyTrue() {
        var result = generateMap().every((k, v) -> v >= 15);
        assertTrue(result);
    }

    @Test
    void everyFalse() {
        var result = generateMap().every((k, v) -> v > 15);
        assertFalse(result);
    }

    @Test
    void someTrue() {
        var result = generateMap().some((k, v) -> v == 15);
        assertTrue(result);
    }

    @Test
    void someFalse() {
        var result = generateMap().some((k, v) -> v == 10);
        assertFalse(result);
    }

    @Test
    void reduce() {
        var result = generateMap().reduce("", (a, k, v) -> a + k + v + " ");
        assertEquals("Bob15 Harry19 Mark24 ", result);

        var resultTwo = generateMap().reduce(0, (a, k, v) -> a + v);
        assertEquals(58, resultTwo);
    }

    @Test
    void intersect() {
        var result = generateMap().intersect(generateAltMap());
        assertEquals(2, result.size());
        assertEquals(15, result.get("Bob"));
        assertEquals(24, result.get("Mark"));
    }

    @Test
    void intersectElements() {
        var result = generateMap().intersectElements(generateAltMap());
        assertEquals(1, result.size());
        assertEquals(15, result.get("Bob"));
    }

    @Test
    void difference() {
        var result = generateMap().difference(generateAltMap());
        assertEquals(2, result.size());
        assertEquals(19, result.get("Harry"));
        assertEquals(19, result.get("Julia"));
    }

    @Test
    void valueList() {
        var result = generateMap().valueList();
        assertEquals(15, result.get(0));
        assertEquals(19, result.get(1));
        assertEquals(24, result.get(2));
    }

    @Test
    void keyList() {
        var result = generateMap().keyList();
        assertEquals("Bob", result.get(0));
        assertEquals("Harry", result.get(1));
        assertEquals("Mark", result.get(2));
    }

    @Test
    void set() {
        var map = new FmMap<>();
        assertEquals(0, map.size());

        map.set("K1", "V1").set("K2", "V2");
        assertEquals(2, map.size());
    }
}