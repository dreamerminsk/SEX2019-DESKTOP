package ch.caro62.parser;

import static org.jsoup.Jsoup.connect;

import ch.caro62.model.Board;
import ch.caro62.model.BoardListPage;
import ch.caro62.model.ModelSource;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

public class App2 {

  public static void main(String... args) throws IOException, SQLException {
    Disposable boobies =
        Flowable.interval(8, TimeUnit.SECONDS)
            .flatMap((id) -> searchBoards("skirt", id.intValue()))
            .flatMap(BoardListParser::parse)
            .takeUntil(boardListPage -> boardListPage.getNext() == null)
            .subscribe(App2::saveBoardList);
    System.in.read();
    boobies.dispose();
  }

  private static void saveBoardList(BoardListPage blp) throws SQLException {
    Dao<Board, String> dao = ModelSource.getBoardDAO();
    try {
      blp.getBoards()
          .forEach(
              board -> {
                try {
                  dao.createIfNotExists(board);
                  System.out.println(board);
                } catch (SQLException e) {
                  e.printStackTrace();
                }
              });
    } finally {
      dao.getConnectionSource().closeQuietly();
    }
  }

  private static Flowable<Document> searchBoards() {
    return searchBoards("boobies", 1);
  }

  private static Flowable<Document> searchBoards(String query, int page) {
    String ref = "https://www.sex.com/search/boards?query=" + query;
    if (page > 0) {
      ref = String.format("%s&page=%d", ref, page + 1);
    }
    System.out.println(page + ": " + ref);
    return getPageAsync(ref);
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
