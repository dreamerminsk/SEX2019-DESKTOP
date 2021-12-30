package ch.caro62.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardListPage {

    private List<Board> boards = new ArrayList<>();

    private String next = "";

    public BoardListPage() {

    }

    public List<Board> getBoards() {
        return Collections.unmodifiableList(boards);
    }

    public void setBoards(List<Board> boards) {
        this.boards = new ArrayList<>();
        this.boards.addAll(boards);
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

}
