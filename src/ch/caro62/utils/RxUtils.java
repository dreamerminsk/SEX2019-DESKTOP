package ch.caro62.utils;

import io.reactivex.Flowable;

public class RxUtils {

    public static Flowable<Long> naturalNumbers() {
        return Flowable.generate(() -> 0L, (state, emitter) -> {
            emitter.onNext(state);
            return state + 1;
        });
    }

}
