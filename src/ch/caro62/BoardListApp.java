package ch.caro62;

import ch.caro62.model.Board;
import ch.caro62.model.BoardListPage;
import ch.caro62.model.ModelSource;
import ch.caro62.parser.BoardListParser;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jsoup.Jsoup.connect;

public class BoardListApp extends JFrame {

    private List<Disposable> disposables = new ArrayList<>();
    private JTextPane textArea;

    public BoardListApp() {
        super("UserApp");
        init();
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disposables.forEach(Disposable::dispose);
            }
        });
    }

    public static void main(String... args) {
        BoardListApp app = new BoardListApp();
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

    private void init() {
        JToolBar toolbar = new JToolBar();

        JTextField searchField = new JTextField();

        toolbar.add(searchField);

        JButton processButton = new JButton("process");
        processButton.addActionListener(e -> process(searchField.getText()));
        toolbar.add(processButton);

        add(toolbar, BorderLayout.NORTH);

        textArea = new JTextPane();
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    private void process(String text) {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        textArea.setText("");
        Disposable boobies = Flowable
                .interval(0, 8, TimeUnit.SECONDS)
                .flatMap((id) -> searchBoards(text, id.intValue()))
                .flatMap(BoardListParser::parse)
                .takeUntil(boardListPage -> boardListPage.getNext() == null)
                .subscribe(this::saveBoardList);
        disposables.add(boobies);
    }

    private void saveBoardList(BoardListPage blp) throws SQLException {
        Dao<Board, String> dao = ModelSource.getBoardDAO();
        try {
            blp.getBoards().forEach(board -> {
                try {
                    Dao.CreateOrUpdateStatus res = dao.createOrUpdate(board);
                    if (res.isCreated()) {
                        appendToPane(textArea, board.getTitle() + " / " + board.getPinCount() +
                                ", " + board.getFollowerCount() + " /\r\n", Color.decode("#006400"));
                        appendToPane(textArea, "\t" + board.getRef() + "\r\n", Color.decode("#006400"));
                    } else {
                        appendToPane(textArea, board.getTitle() + " / " + board.getPinCount() +
                                ", " + board.getFollowerCount() + " /\r\n", Color.decode("#8B0000"));
                        appendToPane(textArea, "\t" + board.getRef() + "\r\n", Color.decode("#8B0000"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            //dao.getConnectionSource().closeQuietly();
        }
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Bold, true);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

}
