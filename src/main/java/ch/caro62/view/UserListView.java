package ch.caro62.view;

import ch.caro62.model.User;
import ch.caro62.service.ImageCache;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

/**
 *
 * @author Karalina Chureyna
 */
public class UserListView extends BorderPane {
    
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    
    public UserListView() {
        super();
        init();
    }

    private void init() {
        GridView<User> grid = new GridView<>();

        grid.setCellHeight(300);
        grid.setCellWidth(280);
        grid.setHorizontalCellSpacing(4);
        grid.setVerticalCellSpacing(4);
        grid.setCellFactory(param -> new UserGridCell());
        grid.setItems(userList);
        setCenter(grid);
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
