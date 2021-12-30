package ch.caro62;

import static org.jsoup.Jsoup.connect;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.parser.UserParser;
import com.j256.ormlite.dao.Dao;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

public class UserApp extends JFrame {

  private List<Disposable> disposables = new ArrayList<>();

  private Map<String, Integer> users = new TreeMap<>();

  public UserApp() {
    super("UserApp");
    init();
    setSize(600, 400);
    setLocationRelativeTo(null);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            disposables.forEach(Disposable::dispose);
          }
        });
  }

  private static Flowable<Document> getUser(User u) {
    String ref = "https://www.sex.com/user/" + u.getRef() + "/";
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

  public static void main(String[] args) {
    UserApp app = new UserApp();
  }

  private void init() {
    JToolBar toolbar = new JToolBar();

    JButton processButton = new JButton("process");

    processButton.addActionListener(
        e -> {
          Disposable d =
              Completable.fromRunnable(this::newUser)
                  .subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.single())
                  .subscribe();
          disposables.add(d);
        });
    toolbar.add(processButton);

    JButton updateButton = new JButton("update");
    updateButton.addActionListener(
        e -> {
          try {
            updateUser();
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
        });
    toolbar.add(updateButton);

    add(toolbar, BorderLayout.NORTH);
  }

  private void updateUser() throws SQLException {
    Disposable boobies =
        Flowable.interval(0, 12, TimeUnit.SECONDS)
            .doOnNext(System.out::println)
            .flatMap((id) -> getNeedUpdate())
            .flatMap(UserApp::getUser)
            .flatMap(UserParser::parse)
            .subscribe(this::saveUser, this::processError);
    disposables.add(boobies);
  }

  private void processError(Throwable throwable) {
    if (throwable.getClass().isAssignableFrom(HttpStatusException.class)) {
      HttpStatusException exc = (HttpStatusException) throwable;
      String url = exc.getUrl();
      String[] items = url.split("/");
      String userRef = items[items.length - 1];
      User user = new User();
      user.setName(userRef);
      user.setRef(userRef);
      try {
        saveUser(user);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void saveUser(User user) throws SQLException {
    Dao<User, String> userDao = ModelSource.getUserDAO();
    userDao.createOrUpdate(user);
  }

  private Flowable<User> getNeedUpdate() throws SQLException {
    Dao<User, String> userDao = ModelSource.getUserDAO();
    return Flowable.fromCallable(
        () -> {
          List<User> us =
              userDao.queryBuilder().selectColumns("ref", "name").where().isNull("name").query();
          return us.get(0);
        });
  }

  private void newUser() {
    try {
      Dao<Board, String> dao = ModelSource.getBoardDAO();
      dao.queryForAll()
          .forEach(
              (b) -> {
                System.out.println(b.getRef());
                String[] items = b.getRef().split("/");
                System.out.println(items[2]);
                users.put(items[2], 0);
              });
      System.out.println(users.size());
      Dao<User, String> userDao = ModelSource.getUserDAO();
      for (Map.Entry<String, Integer> user : users.entrySet()) {
        User u = new User();
        u.setRef(user.getKey());
        userDao.createIfNotExists(u);
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private void newUser2() {
    Flowable.create(emitter -> {}, BackpressureStrategy.BUFFER);
  }
}
