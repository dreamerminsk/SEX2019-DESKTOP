package ch.caro62.parser;

import ch.caro62.model.Board;
import ch.caro62.model.BoardListPage;
import ch.caro62.utils.NumberUtils;
import io.reactivex.Flowable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class BoardListParser {

    public static Flowable<BoardListPage> parse(Document doc) {
        BoardListPage boardListPage = new BoardListPage();

        for (Element ref : doc.select("a")) {
            if (ref.text().trim().equalsIgnoreCase("Next")) {
                boardListPage.setNext(ref.attr("abs:href"));
            }
        }

        List<Board> list = new ArrayList<>();
        for (Element box : doc.select("div.masonry_box")) {
            Board board = new Board();
            for (Element ref : box.select("div.title a")) {
                board.setTitle(ref.text().trim());
                board.setRef(ref.attr("href"));
            }
            for (Element ref : box.select("div.followerCount")) {
                Integer count = NumberUtils.extractNumber(ref.text());
                board.setFollowerCount(count);
            }
            for (Element ref : box.select("div.pinCount")) {
                Integer count = NumberUtils.extractNumber(ref.text());
                board.setPinCount(count);
            }
            list.add(board);
        }
        boardListPage.setBoards(list);

        return Flowable.fromArray(boardListPage);
    }

}
