package ch.caro62.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberUtilsTest {

    @Test
    public void extractNumber() {
        assertEquals("Error parsing", (long) 21, (long) NumberUtils.extractNumber(" 21 followers"));
        assertEquals("Error parsing", (long) 1, (long) NumberUtils.extractNumber(" 1 followers"));
        assertEquals("Error parsing", (long) 0, (long) NumberUtils.extractNumber(" 0 followers"));
        assertEquals("Error parsing", (long) 0, (long) NumberUtils.extractNumber(" followers"));
    }
}