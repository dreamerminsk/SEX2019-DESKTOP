package ch.caro62.kb;

import ch.caro62.kb.model.Models;
import ch.caro62.kb.parser.YearsParser;
import io.reactivex.Flowable;
import org.jsoup.Jsoup;

import java.io.File;
import java.sql.SQLException;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class YearsCrawler {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cache(new Cache(new File("cache"), 10 * 1024 * 1024))
            .connectionPool(new ConnectionPool()).build();

    public static void main(String[] args) throws SQLException {
        Models.createTables();
        getHtml("https://www.kinobusiness.com/kassovye_sbory/films_year/")
                .map(Jsoup::parse)
                .flatMap(YearsParser::parse)
                .doOnNext(Models::saveYears)
                .blockingSubscribe();
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
