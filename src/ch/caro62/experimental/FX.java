package ch.caro62.experimental;

import ch.caro62.model.ModelSource;
import ch.caro62.model.User;
import ch.caro62.model.dao.UserDao;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.sql.SQLException;
import java.util.List;

public class FX extends Application {
    private ObservableList<User> userList;

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
        grid.setCellHeight(160);
        grid.setCellWidth(160);
        grid.setHorizontalCellSpacing(4);
        grid.setVerticalCellSpacing(4);
        grid.setCellFactory(param -> new StringGridCell());
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
            List<User> users = query.limit(16L).query();
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class StringGridCell extends GridCell<User> {

        private final ImageView img;
        private TitledPane userPane;

        public StringGridCell() {
            getStyleClass().add("color-grid-cell"); //$NON-NLS-1$

            userPane = new TitledPane();

            img = new ImageView();

            img.setImage(new Image("https://st.kp.yandex.net/images/sm_film/1097392.jpg"));
            VBox box = new VBox();


            box.getChildren().add(img);
            box.getChildren().add(new Button("like"));
            userPane.setContent(box);
            setGraphic(userPane);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                if (item.getName() != null) {
                    userPane.setText(item.getName());
                } else {
                    userPane.setText(item.getRef());
                }
                if (item.getAvatar() != null) {
                    img.setImage(new Image(item.getAvatar(), 80, 80, false, false));
                }
                setGraphic(userPane);
            }
        }

    }
}
