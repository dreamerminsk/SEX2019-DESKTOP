package ch.caro62.experimental;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class FX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane panel = new FlowPane();
        panel.setHgap(5.0);
        panel.setVgap(4.0);
        for (int i =0; i<5; i++) {
            Button b = new Button("b" + i);
                panel.getChildren().add(b);
        }
        Scene scene = new Scene(panel, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
