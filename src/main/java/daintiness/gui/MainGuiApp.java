package daintiness.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;


// This is just a for gui testing.
public class MainGuiApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        URL location = getClass().getResource("/fxml/Scene.fxml");
        //ResourceBundle resources = ResourceBundle.getBundle("src.main");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Pane root = (Pane)fxmlLoader.load();
        root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setTitle("Ploutarch Parallel Lives");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
