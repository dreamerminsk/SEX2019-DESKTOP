package ch.caro62.experimental;

import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.model.dao.UserDao;
import ch.caro62.parser.UserParser;
import ch.caro62.service.ImageLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
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
import javafx.scene.image.Image;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SexComApp extends Application {

    private ObservableList<User> userList;

    private LoadingCache<String, Image> IMAGE_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(10)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(320)
            .recordStats()
            .softValues()
            .weakKeys()
            .build(new CacheLoader<String, Image>() {
                @Override
                public Image load(String key) throws Exception {
                    System.out.println(key);
                    return new Image(key, true);
                }
            });

    private List<Disposable> updaters = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX App");

        ToolBar toolBar = new ToolBar();

        TextField search = new TextField("");
        search.setPromptText("search.....");
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 2) this.search(newValue);
        });
        toolBar.getItems().add(search);

        Button button1 = new Button("search");
        toolBar.getItems().add(button1);

        BorderPane vBox = new BorderPane();
        vBox.setTop(toolBar);

        GridView<User> grid = new GridView<>();

        grid.setCellHeight(280);
        grid.setCellWidth(280);
        grid.setHorizontalCellSpacing(4);
        grid.setVerticalCellSpacing(4);
        grid.setCellFactory(param -> new UserGridCell());
        userList = FXCollections.observableArrayList();
        UserDao userDao = (UserDao) ModelSource.getUserDAO();
        Flowable<User> randoms = userDao.getRandom(512);

        Disposable updater = randoms
                .flatMap(user -> ImageLoader.getString(user.getAbsRef()))
                .flatMap(html -> Flowable.just(Jsoup.parse(html)))
                .flatMap(UserParser::parse)
                .doOnError(System.out::println)
                .doOnNext(u -> Platform.runLater(() -> userList.add(u)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .retry()
                .subscribe(this::saveUser);
        updaters.add(updater);
        grid.setItems(userList);
        vBox.setCenter(grid);

        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(primaryScreenBounds.getWidth() / 3 * 2);
        primaryStage.setHeight(primaryScreenBounds.getHeight() / 3 * 2);

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        CacheStats stats = IMAGE_CACHE.stats();
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
            Disposable updater = Flowable.fromIterable(users)
                    .flatMap(user -> ImageLoader.getString(user.getAbsRef()))
                    .flatMap(html -> Flowable.just(Jsoup.parse(html)))
                    .flatMap(UserParser::parse)
                    .doOnError(System.out::println)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(this::saveUser);
            updaters.add(updater);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveUser(User user) throws SQLException {
        System.out.println(user.getRef() + ", " + user.getName());
        Dao<User, String> userDao = ModelSource.getUserDAO();
        userDao.createOrUpdate(user);
    }

    private class UserGridCell extends GridCell<User> {

        private ImageView img;
        private ProgressIndicator pi;
        private TitledPane userPane;
        private Hyperlink boards = new Hyperlink();
        private final Hyperlink following = new Hyperlink();

        public UserGridCell() {
            getStyleClass().add("color-grid-cell"); //$NON-NLS-1$

            userPane = new TitledPane();
            userPane.setCollapsible(false);

            img = new ImageView();

            img.setFitHeight(200);
            img.setFitWidth(200);
            img.setPreserveRatio(false);
            img.setSmooth(true);

            pi = new ProgressIndicator();
            //pi.visibleProperty().bind(img.getImage().progressProperty().lessThan(1.0));

            StackPane box = new StackPane();
            StackPane.setAlignment(boards, Pos.TOP_RIGHT);
            box.getChildren().addAll(img, pi, boards);


            VBox vbox = new VBox();
            HBox hbox = new HBox(7);
            hbox.setAlignment(Pos.TOP_CENTER);
            hbox.getChildren().addAll(boards, following);
            vbox.getChildren().add(hbox);
            vbox.getChildren().add(box);


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
                img.setImage(IMAGE_CACHE.getUnchecked(item.getAvatar()));
                pi.visibleProperty().bind(img.getImage().progressProperty().lessThan(1.0));
                pi.progressProperty().bind(img.getImage().progressProperty());
                userPane.setText(item.getRef());
                boards.setText(item.getBoardCount() + " board(s)");
                following.setText(item.getFollowerCount() + " following");
                setGraphic(userPane);
            }
        }

    }
}
