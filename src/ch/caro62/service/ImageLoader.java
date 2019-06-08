package ch.caro62.service;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    private static final Cache cache = new Cache(new File("cache"), 10 * 1024 * 1024);

    private static final OkHttpClient ok = new OkHttpClient.Builder()
            .cache(cache)
            .build();

    public static Flowable<InputStream> getBytes(String ref) {
        Request request = new Request.Builder()
                .url(ref)
                .build();
        return Flowable.create(emitter -> {
            try {
                Response response = ok.newCall(request).execute();
                emitter.onNext(response.body().byteStream());
                response.body().close();
                emitter.onComplete();
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    public static Flowable<String> getString(String ref) {
        Request request = new Request.Builder()
                .url(ref)
                .build();
        return Flowable.create(emitter -> {
            try {
                Response response = ok.newCall(request).execute();
                emitter.onNext(response.body().string());
                response.body().close();
                emitter.onComplete();
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

}
