package ch.caro62.kb.parser;

import ch.caro62.kb.model.Movie;
import ch.caro62.kb.model.Years;
import ch.caro62.utils.NumberUtils;
import io.reactivex.Flowable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class YearsParser {

  public static Flowable<Years> parse(Document doc) {
    Movie m = new Movie();
    Years y = new Years();
    return Flowable.just(doc)
        .flatMap(d -> Flowable.fromIterable(d.select("table#krestable tr")))
        .skip(1)
        .flatMap(YearsParser::parseYear);
  }

  private static Flowable<Years> parseYear(Element item) {
    Years y = new Years();
    y.setYear(2019);
    Movie m = new Movie();
    y.setMovie(m);
    Elements cells = item.select("td");
    for (int i = 0; i < cells.size(); i++) {
      Element cell = cells.get(i);
      switch (i) {
        case 0:
          y.setRank(NumberUtils.extractNumberWithZeroes(cell.text()));
          break;
        case 1:
          Element ref = cell.selectFirst("a");
          m.setId(NumberUtils.extractNumberWithZeroes(ref.attr("name")));
          m.setTitle(ref.text().trim());
          m.setRef(ref.attr("href"));
          System.out.println(y.getMovie().getTitle());
          break;
        case 2:
          m.setOriginal(cell.text().trim());
          break;
        case 4:
          y.setScreens(NumberUtils.extractNumberWithZeroes(cell.text()));
          break;
        case 5:
          y.setBoxOfficeRur(NumberUtils.extractNumberWithZeroes(cell.text()));
          break;
        case 6:
          y.setBoxOfficeUsd(NumberUtils.extractNumberWithZeroes(cell.text()));
          break;
        case 7:
          y.setViewers(NumberUtils.extractNumberWithZeroes(cell.text()));
          break;
        case 8:
          y.setDays(NumberUtils.extractNumberWithZeroes(cell.text()));
          break;
      }
    }
    return Flowable.just(y);
  }
}
