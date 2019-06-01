package ch.caro62.experimental;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.*;

public class WordStats {

    private static final Map<String, Integer> Words = new TreeMap<>();

    public static void main(String[] args) throws SQLException {
        Dao<Board, String> boardDao = ModelSource.getBoardDAO();
        List<Board> boards = boardDao.queryForAll();
        for (Board board : boards) {
            String title = board.getTitle().toLowerCase();
            String word = "";
            for (Character ch : title.toCharArray()) {
                if (Character.isLetterOrDigit(ch)) {
                    word += ch;
                } else {
                    if (word.length() > 3) {
                        if (Words.containsKey(word)) {
                            Words.put(word, Words.get(word) + board.getFollowerCount());
                        } else {
                            Words.put(word, board.getFollowerCount());
                        }
                    }
                    word = "";
                }
            }
        }
        for (Map.Entry<String, Integer> word : Words.entrySet()) {
            //System.out.println(word.getKey() + " " + word.getValue());
        }

        Map<String, Integer> sortWords = sortByValue(Words);
        for (Map.Entry<String, Integer> word : sortWords.entrySet()) {
            System.out.println(word.getKey() + " " + word.getValue());
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
