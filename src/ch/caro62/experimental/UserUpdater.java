package ch.caro62.experimental;

import ch.caro62.model.Board;
import ch.caro62.model.BoardListPage;
import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.model.dao.impl.UserDaoImpl;
import ch.caro62.parser.BoardListParser;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jsoup.Jsoup.connect;

public class UserUpdater extends JFrame {

    private List<Disposable> disposables = new ArrayList<>();

    private JTextPane textArea;

    private JPopupMenu menu = new JPopupMenu();

    public UserUpdater() {
        super("UserUpdater");
        init();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disposables.forEach(Disposable::dispose);
            }
        });
        Disposable d = Flowable.timer(8, TimeUnit.SECONDS)
                .flatMap((idx) -> {
                    UserDaoImpl dao = (UserDaoImpl) ModelSource.getUserDAO();
                    return dao.getRandom();
                })

                .map((u) -> "https://sex.com/user/" + u.getRef() + "/following/")
                .flatMap(UserUpdater::getPageAsync)
                .flatMap(BoardListParser::parse)
                //.takeUntil(boardListPage -> boardListPage.getNext() == null)
                .repeat()
                .subscribe(this::saveBoardList);
        disposables.add(d);
    }

    private void boards(User user) {
        Flowable.just(user)
                .map(u -> String.format("https://sex.com/user/%s/", u.getRef()));
    }

    private void saveBoardList(BoardListPage blp) throws SQLException {
        Dao<Board, String> dao = ModelSource.getBoardDAO();
        try {
            blp.getBoards().forEach(board -> {
                try {
                    Dao.CreateOrUpdateStatus res = dao.createOrUpdate(board);
                    if (res.isCreated()) {
                        appendToPane(textArea, board.getTitle() + " / " + board.getPinCount() +

                                ", " + board.getFollowerCount() + " /\r\n", Color.decode("#e88000"));
                        appendToPane(textArea, "\t" + board.getRef() + "\r\n", Color.decode("#e88000"));
                    } else {
                        appendToPane(textArea, board.getTitle() + " / " + board.getPinCount() +
                                ", " + board.getFollowerCount() + " /\r\n", Color.decode("#0068e8"));
                        appendToPane(textArea, "\t" + board.getRef() + "\r\n", Color.decode("#0068e8"));

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            //dao.getConnectionSource().closeQuietly();
        }
    }

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            UserUpdater app = new UserUpdater();
        });

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

        initMenu();

        JToolBar toolbar = new JToolBar();

        JTextField searchField = new JTextField();
        searchField.setComponentPopupMenu(menu);
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //process(searchField.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        toolbar.add(searchField);

        JButton processButton = new JButton("process");
        //processButton.addActionListener(e -> process(searchField.getText()));
        toolbar.add(processButton);

        add(toolbar, BorderLayout.NORTH);

        textArea = new JTextPane();
        textArea.setComponentPopupMenu(menu);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    private void initMenu() {
        addAction(new DefaultEditorKit.CutAction(), KeyEvent.VK_X, "Cut");
        addAction(new DefaultEditorKit.CopyAction(), KeyEvent.VK_C, "Copy");
        addAction(new DefaultEditorKit.PasteAction(), KeyEvent.VK_V, "Paste");
    }

    private void addAction(TextAction action, int key, String text) {
        action.putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        action.putValue(AbstractAction.NAME, text);
        menu.add(new JMenuItem(action));
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
