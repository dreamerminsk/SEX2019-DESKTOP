package ch.caro62.kb;

import io.reactivex.Flowable;
import okhttp3.*;
import org.jsoup.Jsoup;

import java.io.File;

public class YearsCrawler {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cache(new Cache(new File("cache"), 10 * 1024 * 1024))
            .connectionPool(new ConnectionPool()).build();

    public static void main(String[] args) {
        getHtml("https://www.kinobusiness.com/kassovye_sbory/films_year/")
                .map(Jsoup::parse)
                .subscribe(System.out::println);
    }

    private static Flowable<String> getHtml(String ref) {
        return Flowable.defer(() -> {
            Request req = new Request.Builder().url(ref).get()
                    .header("User-Agent", "Firefox")
                    .build();
            Response res = CLIENT.newCall(req).execute();
            try (ResponseBody body = res.body()) {
                return Flowable.just(body.string());
            } catch (Exception e) {
                return Flowable.error(e);
            }
        });
    }

}
