package ch.caro62.experimental;

import ch.caro62.model.Board;
import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.model.dao.BoardDao;
import ch.caro62.model.dao.UserDao;
import ch.caro62.parser.UserParser;
import ch.caro62.service.ImageCache;
import ch.caro62.service.NetLoader;
import ch.caro62.service.RequestInfo;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class SexComApp extends Application {

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    private final List<Disposable> updaters = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }
    private Hyperlink userFound;
    private Hyperlink boardFound;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX App");

        ToolBar toolBar = new ToolBar();

        TextField search = new TextField("");
        search.setPromptText("search.....");
        search.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.length() > 1) {
                SexComApp.this.search(newValue);
            }
        });
        toolBar.getItems().add(search);

        userFound = new Hyperlink();
        userFound.textProperty().bind(Bindings.size(userList).asString().concat(" users"));

        boardFound = new Hyperlink();

        toolBar.getItems().addAll(userFound, boardFound);

        Button button1 = new Button("random");
        button1.setOnAction((e) -> {
            try {
                random();
            } catch (SQLException ex) {
                Logger.getLogger(SexComApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        toolBar.getItems().add(button1);
        
        Button button2 = new Button("okHttp");
        button2.setOnAction((e) -> {
            second(primaryStage);
        });
        toolBar.getItems().add(button2);

        BorderPane vBox = new BorderPane();
        vBox.setTop(toolBar);

        GridView<User> grid = new GridView<>();

        grid.setCellHeight(300);
        grid.setCellWidth(280);
        grid.setHorizontalCellSpacing(4);
        grid.setVerticalCellSpacing(4);
        grid.setCellFactory(param -> new UserGridCell());

        random();
        grid.setItems(userList);
        vBox.setCenter(grid);

        Scene scene = new Scene(vBox);

        primaryStage.setScene(scene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(primaryScreenBounds.getWidth() / 3 * 2);
        primaryStage.setHeight(primaryScreenBounds.getHeight() / 3 * 2);

        primaryStage.show();
    }

    private void second(Stage primaryStage) {
        BorderPane root = new BorderPane();
        TableView<RequestInfo> table = new TableView<>();
        TableColumn<RequestInfo, LocalDateTime> startedCol = new TableColumn<>("started");
        TableColumn<RequestInfo, String> refCol = new TableColumn<>("ref");
        TableColumn<RequestInfo, String> exCol = new TableColumn<>("exception");
        TableColumn<RequestInfo, String> titleCol = new TableColumn<>("title");
        table.getColumns().addAll(startedCol, refCol, exCol, titleCol);
        startedCol.setCellValueFactory(new PropertyValueFactory<>("started"));
        refCol.setCellValueFactory(new PropertyValueFactory<>("ref"));
        exCol.setCellValueFactory(new PropertyValueFactory<>("exception"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        table.setItems(NetLoader.getReqs());
        root.setCenter(table);
        Scene secondScene = new Scene(root, 800, 400);
        Stage secondStage = new Stage();
        secondStage.setTitle("okHttpClient requests");
        secondStage.setScene(secondScene);
        secondStage.initStyle(StageStyle.DECORATED);
        secondStage.initModality(Modality.NONE);
        secondStage.initOwner(primaryStage);
        primaryStage.toFront();
        secondStage.show();
    }

    private void random() throws SQLException {
        Platform.runLater(() -> {
            boardFound.setText("0 boards");
        });
        updaters.forEach(Disposable::dispose);
        Platform.runLater(userList::clear);
        UserDao userDao = (UserDao) ModelSource.getUserDAO();
        Flowable<User> randoms = userDao.getRandomUnlim();

        Disposable updater = randoms
                .doOnNext(u -> Platform.runLater(() -> userList.add(u)))
                .filter((User u) -> LocalDateTime.now()
                .compareTo(
                        u.getlastChecking().plusDays(1)) > 0)
                .flatMap(user -> NetLoader.getString(user.getAbsRef()))
                .flatMap(html -> Flowable.just(Jsoup.parse(html)))
                .flatMap(UserParser::parse)
                .filter((User u) -> u.getRef() != null)
                .map((User u) -> {
                    u.setlastChecking(LocalDateTime.now());
                    return u;
                })
                .doOnError(System.out::println)
                .doOnNext(this::updateUser)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .retry()
                .subscribe(this::saveUser);
        updaters.add(updater);
    }

    @Override
    public void stop() throws Exception {
        CacheStats stats = ImageCache.getCache().stats();
        System.out.println(stats);
        super.stop();
    }

    private void search(String newValue) {
        try {
            updaters.forEach(Disposable::dispose);
            UserDao userDao = (UserDao) ModelSource.getUserDAO();
            QueryBuilder<User, String> query = userDao.queryBuilder();
            Where<User, String> where = query.where();
            where.like("ref", "%" + newValue + "%");
            List<User> users = query.query();
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
            });

            BoardDao boardDao = (BoardDao) ModelSource.getBoardDAO();
            QueryBuilder<Board, String> bquery = boardDao.queryBuilder();
            Where<Board, String> bwhere = bquery.where();
            bwhere.like("title", "%" + newValue + "%");
            List<Board> boards = bquery.query();
            Platform.runLater(() -> {
                boardFound.setText(boards.size() + " boards");
            });

            Disposable updater = Flowable.fromIterable(users)
                    .filter((User u) -> LocalDateTime.now()
                    .compareTo(
                            u.getlastChecking().plusDays(1)) > 0)
                    .flatMap(user -> NetLoader.getString(user.getAbsRef()))
                    .flatMap(html -> Flowable.just(Jsoup.parse(html)))
                    .flatMap(UserParser::parse)
                    .filter((User u) -> u.getRef() != null)
                    .map((User u) -> {
                        u.setlastChecking(LocalDateTime.now());
                        return u;
                    })
                    .doOnNext(this::updateUser)
                    .doOnError(System.out::println)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(this::saveUser);
            updaters.add(updater);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void settings() throws SQLException {
        UserDao userDao = (UserDao) ModelSource.getUserDAO();
        Flowable
                .fromIterable(userDao.queryForAll())
                .filter(u -> u.getlastChecking() != null)
                .doOnNext(u -> {
                    Random r = new Random();
                    u.setlastChecking(LocalDateTime.now().minusDays(r.nextInt(600)));
                    userDao.update(u);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe();
    }

    private void saveUser(User user) throws SQLException {
        System.out.println(user.getRef() + ", " + user.getName());
        Dao<User, String> userDao = ModelSource.getUserDAO();
        userDao.createOrUpdate(user);
    }

    private void updateUser(User u) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getRef().equalsIgnoreCase(u.getRef())) {
                final int index = i;
                Platform.runLater(() -> {
                    userList.set(index, u);
                });

            }
        }
    }

    private class UserGridCell extends GridCell<User> {

        private final ImageView img;
        private final ProgressIndicator pi;
        private final TitledPane userPane;
        private final Hyperlink boards = new Hyperlink();
        private final Hyperlink following = new Hyperlink();
        private final Hyperlink pins = new Hyperlink();
        private final Hyperlink repins = new Hyperlink();

        public UserGridCell() {
            userPane = new TitledPane();
            userPane.setCollapsible(false);

            img = new ImageView();
            img.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
            img.setFitHeight(200);
            img.setFitWidth(200);
            img.setPreserveRatio(true);
            img.setSmooth(true);

            pi = new ProgressIndicator();
            //pi.visibleProperty().bind(img.getImage().progressProperty().lessThan(1.0));

            StackPane box = new StackPane();

            VBox vbox = new VBox(2.5);
            vbox.getChildren().add(box);
            VBox statsPane = new VBox();
            statsPane.setBorder(Border.EMPTY);
            statsPane.setOpacity(0.95);
            statsPane.setStyle("-fx-background-color: -fx-box-border, -fx-inner-border, -fx-body-color;\n"
                    + "-fx-background-insets: 0, 1, 2;\n"
                    + "-fx-background-radius: 5 5 0 0, 4 4 0 0, 3 3 0 0, 2 2 0 0;\n"
                    + "-fx-padding: 0.166667em 0.833333em 0.25em 0.833333em; /* 2 10 3 10 */");
            HBox hbox = new HBox(3);
            hbox.setAlignment(Pos.TOP_CENTER);
            hbox.getChildren().addAll(boards, following);
            statsPane.getChildren().add(hbox);
            HBox hbox2 = new HBox(3);
            hbox2.setAlignment(Pos.TOP_CENTER);
            hbox2.getChildren().addAll(pins, repins);
            statsPane.getChildren().add(hbox2);
            vbox.getChildren().add(statsPane);

            //StackPane.setAlignment(statsPane, Pos.BOTTOM_CENTER);
            box.getChildren().addAll(img, pi);

            userPane.setContent(vbox);
            setGraphic(userPane);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                img.setImage(ImageCache.getCache().get(item.getAvatar()));
                pi.visibleProperty().bind(img.getImage().progressProperty().lessThan(1.0));
                pi.progressProperty().bind(img.getImage().progressProperty());
                userPane.setText(item.getName() == null ? "/user/" + item.getRef()
                        : item.getName() + " " + item.getlastChecking().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                boards.setText(item.getBoardCount() + " board(s)");
                following.setText(item.getFollowerCount() + " following");
                pins.setText(item.getPinCount() + " pin(s)");
                repins.setText(item.getRepinCount() + " repin(s)");
                setGraphic(userPane);
            }
        }

    }
}
