package ch.caro62.experimental;

import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.model.dao.UserDao;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SexComApp extends Application {
    private ObservableList<User> userList;
    private LoadingCache<String, Image> IMAGE_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(10)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(160)
            .recordStats()
            .softValues()
            .weakKeys()
            .build(new CacheLoader<String, Image>() {
                @Override
                public Image load(String key) throws Exception {
                    return new Image(key, true);
                }
            });

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

        grid.setCellHeight(250);
        grid.setCellWidth(250);
        grid.setHorizontalCellSpacing(4);
        grid.setVerticalCellSpacing(4);
        grid.setCellFactory(param -> new UserGridCell());
        userList = FXCollections.observableArrayList();
        for (int i = 0; i < 12; i++) {
            UserDao userDao = (UserDao) ModelSource.getUserDAO();
            userList.add(userDao.getRandom().blockingSingle());
        }
        grid.setItems(userList);
        vBox.setCenter(grid);

        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void search(String newValue) {
        try {
            UserDao userDao = (UserDao) ModelSource.getUserDAO();
            QueryBuilder<User, String> query = userDao.queryBuilder();
            Where<User, String> where = query.where();
            where.like("ref", "%" + newValue + "%");
            List<User> users = query.limit(64L).query();
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class UserGridCell extends GridCell<User> {

        private final Text text;
        private ImageView img;
        private ProgressIndicator pi;
        private TitledPane userPane;

        public UserGridCell() {
            getStyleClass().add("color-grid-cell"); //$NON-NLS-1$

            userPane = new TitledPane();
            userPane.setCollapsible(false);

            img = new ImageView(IMAGE_CACHE.getUnchecked("https://www.sex.com/images/default_profile_picture.png"));

            img.setFitHeight(160);
            img.setFitWidth(160);
            img.setPreserveRatio(true);
            img.setSmooth(true);

            pi = new ProgressIndicator();
            pi.visibleProperty().bind(img.getImage().progressProperty().lessThan(1.0));

            StackPane box = new StackPane();
            box.getChildren().addAll(img, pi);

            VBox vbox = new VBox();
            vbox.getChildren().add(box);
            text = new Text();
            vbox.getChildren().add(text);

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
                userPane.setText(item.getRef());
                text.setText(item.getRef());
                setGraphic(userPane);
            }
        }

    }
}
