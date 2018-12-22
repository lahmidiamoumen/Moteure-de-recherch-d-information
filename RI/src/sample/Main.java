package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/home.fxml"));
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");
        this.primaryStage = primaryStage;
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
        this.primaryStage.setTitle("Search Engine");
        this.primaryStage.setResizable(true);
        //primaryStage.setMaximized(true);
        this.primaryStage.setScene(new Scene(root, 1080, 750));
        this.primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
