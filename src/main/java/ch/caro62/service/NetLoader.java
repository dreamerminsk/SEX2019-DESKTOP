package ch.caro62.service;

import com.google.common.util.concurrent.RateLimiter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NetLoader {

  private static final ObservableList<RequestInfo> REQS = FXCollections.observableArrayList();

  private static final Random RANDOM = new Random(System.nanoTime());

  private static final RateLimiter LIMITER = RateLimiter.create(1000);

  private static final Cache CACHE = new Cache(new File("cache"), 32 * 1024 * 1024);

  private static final OkHttpClient OK = new OkHttpClient.Builder().cache(CACHE).build();

  public static Flowable<InputStream> getBytes(String ref) {
    LIMITER.acquire(RANDOM.nextInt(640));
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
    final RequestInfo requestInfo = new RequestInfo(request);
    return Flowable.create(
        emitter -> {
          Response response = null;
          try {
            LIMITER.acquire(4000 + RANDOM.nextInt(8000));
            Call call = OK.newCall(request);
            call.timeout().timeout(0, TimeUnit.SECONDS);
            response = call.execute();
            String html = response.body().string();
            emitter.onNext(html);
            response.body().close();
            emitter.onComplete();
            Document doc = Jsoup.parse(html);
            final Response res = response;
            doc.select("title")
                .forEach(
                    t -> {
                      requestInfo.setTitle(
                          res.message() + " " + html.length() + " " + t.text().trim());
                    });
            REQS.add(requestInfo);
          } catch (IOException e) {
            requestInfo.exception(e.getClass().getCanonicalName());
            REQS.add(requestInfo);
            emitter.onError(e);
          }
        },
        BackpressureStrategy.LATEST);
  }

  public static ObservableList<RequestInfo> getReqs() {
    return REQS;
  }
}
