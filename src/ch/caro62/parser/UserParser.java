package ch.caro62.parser;

import ch.caro62.model.User;
import ch.caro62.utils.NumberUtils;
import io.reactivex.Flowable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UserParser {

    public static Flowable<User> parse(Document doc) {
        User user = new User();

        Element userBox = doc.selectFirst("div.user_info_box");
        for (Element ref : userBox.select("h1")) {
            user.setName(ref.text().trim());
        }

        for (Element ref : userBox.select("div.user_profile_picture img")) {
            user.setAvatar(ref.attr("abs:src"));
        }

        for (Element ref : userBox.select("div.description")) {
            user.setDescription(ref.text().trim());
        }

        Element boardBox = doc.selectFirst("div.create_board_box");

        for (Element ref : boardBox.select("li a")) {
            Integer count = NumberUtils.extractNumber(ref.text());
            if (ref.text().contains("Boards")) {
                String[] locItems = ref.attr("href").split("/");
                System.out.println(ref.attr("href") + ", " + ref.text());
                user.setRef(locItems[locItems.length - 1]);
                user.setBoardCount(count);
            } else if (ref.text().contains("Following")) {
                user.setFollowerCount(count);
            } else if (ref.text().contains("Pins")) {
                user.setPinCount(count);
            } else if (ref.text().contains("Repins")) {
                user.setRepinCount(count);
            } else if (ref.text().contains("Likes")) {
                user.setLikeCount(count);
            }
        }

        return Flowable.just(user);
    }

}
