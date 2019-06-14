package ch.caro62.experimental;

import javafx.application.Application;
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

public class FX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX App");

        ToolBar toolBar = new ToolBar();

        TextField search = new TextField("Search...");
        search.textProperty().addListener((observable, oldValue, newValue) -> new Alert(Alert.AlertType.INFORMATION, search.getText()).showAndWait());
        toolBar.getItems().add(search);

        Button button1 = new Button("Button 1");
        toolBar.getItems().add(button1);

        BorderPane vBox = new BorderPane();
        vBox.setTop(toolBar);

        GridView<String> grid = new GridView<String>();
        grid.setCellHeight(160);
        grid.setCellWidth(160);
        grid.setHorizontalCellSpacing(4);
        grid.setVerticalCellSpacing(4);
        //grid.setBorder(new Border());
        grid.setCellFactory(param -> {
            GridCell<String> item = new StringGridCell();

            return item;
        });
        ObservableList<String> text = FXCollections.observableArrayList();
        for (int i = 0; i < 12; i++) {
            text.add("--==TEST==--");
        }
        grid.setItems(text);
        vBox.setCenter(grid);

        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private class StringGridCell extends GridCell<String> {

        private TitledPane colorRect;

        public StringGridCell() {
            getStyleClass().add("color-grid-cell"); //$NON-NLS-1$

            colorRect = new TitledPane();

            ImageView img = new ImageView();

            img.setImage(new Image("https://st.kp.yandex.net/images/sm_film/1097392.jpg"));
            VBox box = new VBox();


            box.getChildren().add(img);
            box.getChildren().add(new Button("Content"));
            colorRect.setContent(box);
            setGraphic(colorRect);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                colorRect.setText(item);
                setGraphic(colorRect);
            }
        }

    }
}
