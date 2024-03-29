package ch.caro62.experimental;

import static ch.caro62.utils.DateTimeUtils.daysHoursMinutes;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static org.jsoup.Jsoup.connect;

import ch.caro62.model.Board;
import ch.caro62.model.BoardListPage;
import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.model.dao.impl.UserDaoImpl;
import ch.caro62.parser.BoardListParser;
import ch.caro62.parser.UserParser;
import ch.caro62.ui.BoardModel;
import ch.caro62.ui.StatsModel;
import ch.caro62.utils.RxUtils;
import ch.caro62.view.BoardView;
import ch.caro62.view.UserView;
import com.j256.ormlite.dao.Dao;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;
import javax.swing.text.*;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel;

public class UserUpdater extends JFrame {

  private static final Color NewItemColor = Color.decode("#e88000");
  private static final Color OldItemColor = Color.decode("#0068e8");
  private DateTime started = DateTime.now();
  private AtomicReference<String> currentUser = new AtomicReference<>("");
  private List<Disposable> disposables = new ArrayList<>();
  private JTextPane textArea;
  private JPopupMenu menu = new JPopupMenu();
  private StatsModel model;
  private BoardModel boardModel;
  private JTable boardTable;

  public UserUpdater() {
    super("UserUpdater");
    init();
    setSize(800, 600);
    setLocationRelativeTo(null);
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            disposables.forEach(Disposable::dispose);
          }
        });
    Disposable disposable =
        Flowable.interval(0, 1, TimeUnit.SECONDS).subscribe(idx -> setAppTitle());
    disposables.add(disposable);
    Disposable d =
        Flowable.timer(8, TimeUnit.SECONDS)
            .flatMap(
                (idx) -> {
                  UserDaoImpl dao = (UserDaoImpl) ModelSource.getUserDAO();
                  return dao.getRandom();
                })
            .doOnNext((item) -> currentUser.set(item.getRef()))
            .map(u -> String.format("https://sex.com/user/%s/", u.getRef()))
            .flatMap(UserUpdater::getPageAsync)
            .flatMap(UserParser::parse)
            .doOnNext(this::saveUser)
            .flatMap((u) -> boards(u.getAbsRef()).mergeWith(boards(u.getAbsRef() + "following/")))
            .repeat()
            .doOnError((e) -> System.out.println(e.getLocalizedMessage()))
            .retry()
            .subscribe(this::saveBoardList);
    disposables.add(d);
  }

  public static void main(String... args) {
    try {
      UIManager.setLookAndFeel(new SubstanceCremeCoffeeLookAndFeel());
      JFrame.setDefaultLookAndFeelDecorated(true);
      JDialog.setDefaultLookAndFeelDecorated(true);
      UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    SwingUtilities.invokeLater(
        () -> {
          UserUpdater app = new UserUpdater();
        });
  }

  private static Flowable<Document> getPageAsync(String ref) {
    return Flowable.fromCallable(() -> getPage(ref));
  }

  private static Document getPage(String ref) throws IOException {
    System.out.println(ref);
    Connection conn = connect(ref);
    try {
      return conn.get();
    } catch (IOException e) {
      throw e;
    }
  }

  private void setAppTitle() {
    SwingUtilities.invokeLater(
        () -> {
          Period diff = new Period(started, DateTime.now());
          this.setTitle(currentUser.get() + " - UserUpdater / " + daysHoursMinutes.print(diff));
        });
  }

  private Flowable<BoardListPage> boards(String ref) {
    AtomicReference<String> next = new AtomicReference<>(ref);
    Random random = new Random();
    return RxUtils.naturalNumbers()
        .concatMap(item -> Flowable.just(item).delay(4 + random.nextInt(8), TimeUnit.SECONDS))
        .map(idx -> next.getAndSet(""))
        .filter(item -> item.length() > 0)
        .flatMap(UserUpdater::getPageAsync)
        .flatMap(BoardListParser::parse)
        .doOnNext(blp -> next.set(blp.getNext()))
        .takeUntil(boardListPage -> boardListPage.getNext().length() == 0)
        .doOnError(e -> System.out.println(e.getMessage()));
  }

  private void saveUser(User u) throws SQLException {
    System.out.println(u.getRef() + ", " + u.getName());
    Dao<User, String> userDao = ModelSource.getUserDAO();
    userDao.createOrUpdate(u);
  }

  private void saveBoardList(BoardListPage blp) throws SQLException {
    Dao<Board, String> boardDAO = ModelSource.getBoardDAO();
    Dao<User, String> userDao = ModelSource.getUserDAO();
    blp.getBoards()
        .forEach(
            board -> {
              try {
                String user = board.getRef().split("/")[2];
                User u = new User();
                u.setRef(user);
                userDao.createIfNotExists(u);
                board.setUser(user);
                Dao.CreateOrUpdateStatus res = boardDAO.createOrUpdate(board);
                if (res.isCreated()) {
                  model.addNewBoard(board.getPinCount(), board.getFollowerCount());
                  boardModel.addBoard(board);
                } else {
                  model.addExistingBoard(board.getPinCount(), board.getFollowerCount());
                  boardModel.addBoard(board);
                }
              } catch (SQLException e) {
                e.printStackTrace();
              }
            });
  }

  private void init() {

    initMenu();

    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    textArea = new JTextPane();
    textArea.setComponentPopupMenu(menu);
    model = new StatsModel();
    JTable table = new JTable(model);

    table.setFont(table.getFont().deriveFont(11.75f));
    JScrollPane tablePanel = new JScrollPane(table);
    tablePanel.setBorder(BorderFactory.createTitledBorder("boards stats"));
    mainSplitPane.setTopComponent(tablePanel);
    boardModel = new BoardModel();
    boardTable = new JTable(boardModel);
    boardTable.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              JTable target = (JTable) e.getSource();
              int row = target.getSelectedRow();
              int column = target.getSelectedColumn();
              if (column == 0) {
                BoardView view = new BoardView();
                view.addBackListener(
                    (event) -> {
                      mainSplitPane.setBottomComponent(new JScrollPane(boardTable));
                      mainSplitPane.setDividerLocation(100);
                    });
                mainSplitPane.setBottomComponent(view);
                mainSplitPane.setDividerLocation(100);
              } else if (column == 1) {
                Dao<User, String> userDao = null;
                try {
                  userDao = ModelSource.getUserDAO();
                  User user =
                      userDao
                          .queryBuilder()
                          .where()
                          .eq("ref", target.getValueAt(row, column))
                          .queryForFirst();
                  UserView view = new UserView(user);
                  view.addBackListener(
                      (event) -> {
                        mainSplitPane.setBottomComponent(new JScrollPane(boardTable));
                        mainSplitPane.setDividerLocation(100);
                      });
                  mainSplitPane.setBottomComponent(view);
                  mainSplitPane.setDividerLocation(100);
                } catch (SQLException ex) {
                  ex.printStackTrace();
                }
              }
            }
          }
        });
    mainSplitPane.setBottomComponent(new JScrollPane(boardTable));
    mainSplitPane.setDividerLocation(100);
    add(mainSplitPane, BorderLayout.CENTER);
  }

  private void initMenu() {
    addAction(new DefaultEditorKit.CutAction(), KeyEvent.VK_X, "Cut");
    addAction(new DefaultEditorKit.CopyAction(), KeyEvent.VK_C, "Copy");
    addAction(new DefaultEditorKit.PasteAction(), KeyEvent.VK_V, "Paste");
  }

  private void addAction(TextAction action, int key, String text) {
    action.putValue(
        AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
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
