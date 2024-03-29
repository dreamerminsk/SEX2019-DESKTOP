package ch.caro62.service;

import com.google.common.util.concurrent.RateLimiter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import okhttp3.*;

public class ImageLoader {

  private static final RateLimiter LIMITER = RateLimiter.create(1000);

  private static final Cache CACHE = new Cache(new File("cache"), 32 * 1024 * 1024);

  private static final OkHttpClient OK = new OkHttpClient.Builder().cache(CACHE).build();

  public static Flowable<InputStream> getBytes(String ref) {
    LIMITER.acquire(400);
    System.out.println("getBytes(\"" + ref + "\")");
    Request request = new Request.Builder().url(ref).build();
    return Flowable.create(
        emitter -> {
          Response response = null;
          try {
            response = OK.newCall(request).execute();
            emitter.onNext(response.body().byteStream());
            response.body().close();
            emitter.onComplete();
          } catch (IOException e) {
            Alert alert =
                new Alert(AlertType.ERROR, ref + "\r\n" + response.message(), ButtonType.OK);
            alert.showAndWait();
            emitter.onError(e);
          }
        },
        BackpressureStrategy.LATEST);
  }

  public static Flowable<String> getString(String ref) {
    Request request =
        new Request.Builder()
            .url(ref)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/70.0.3538.77 Safari/537.36")
            .build();
    return Flowable.create(
        emitter -> {
          Response response = null;
          try {
            LIMITER.acquire(4257);
            Call call = OK.newCall(request);
            call.timeout().timeout(0, TimeUnit.SECONDS);
            response = call.execute();
            emitter.onNext(response.body().string());
            response.body().close();
            emitter.onComplete();
          } catch (IOException e) {
            Platform.runLater(
                () -> {
                  Alert alert =
                      new Alert(AlertType.ERROR, ref + "\r\n" + e.getMessage(), ButtonType.OK);
                  alert.showAndWait();
                });

            emitter.onError(e);
          }
        },
        BackpressureStrategy.LATEST);
  }
}
