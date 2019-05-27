package ch.caro62.utils;

import hu.akarnokd.rxjava2.string.StringObservable;

public class NumberUtils {

    private final static int ZERO_CODE = '0';

    public static Integer extractNumber(String value) {
        return StringObservable
                .characters(value)
                .skipWhile(integer -> !Character.isDigit(integer))
                .takeWhile(Character::isDigit)
                .map((d) -> d - ZERO_CODE)
                .reduce(0, (x, y) -> 10 * x + y)
                .blockingGet();
    }

}
