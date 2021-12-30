package ch.caro62;

import ch.caro62.model.BoardListPage;
import ch.caro62.parser.BoardListParser;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static org.jsoup.Jsoup.connect;

public class App {

    public static void main(String[] args) {
        Flowable<Document> fl = getPageAsync("https://www.sex.com/search/boards?query=boobies");
        DisposableSubscriber<BoardListPage> d = fl
                .observeOn(Schedulers.single())
                .subscribeOn(Schedulers.io())
                .flatMap(BoardListParser::parse)
                .subscribeWith(new DisposableSubscriber<BoardListPage>() {
                    @Override
                    public void onNext(BoardListPage document) {
                        System.out.println(document.getNext());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("complete");
                    }
                });
        try {
            Thread.sleep(15000);
            d.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Flowable<Document> getPageAsync(String ref) {
        return Flowable.fromCallable(() -> getPage(ref));
    }

    private static Document getPage(String ref) throws IOException {
        Connection conn = connect(ref);
        try {
            return conn.get();
        } catch (IOException e) {
            throw e;
        }
    }

}
