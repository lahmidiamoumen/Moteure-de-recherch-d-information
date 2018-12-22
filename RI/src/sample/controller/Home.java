package sample.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sample.Main;
import sample.model.Doc;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Home implements Initializable {
    @FXML
    Button close,minimise,maximise;
    @FXML
    BorderPane holdPane;
    BorderPane searchPane,queryPane,showPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            searchPane = FXMLLoader.load(getClass().getResource("../view/search.fxml"));
            queryPane = FXMLLoader.load(getClass().getResource("../view/query.fxml"));
            showPane = FXMLLoader.load(getClass().getResource("../view/show.fxml"));

            setNode(searchPane);
        } catch (IOException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }


        eventSearchPane();
        close.setOnAction(event -> System.exit(0));

        minimise.setOnAction(e -> Main.primaryStage.setIconified(true));
        maximise.setOnAction(e -> {
            if (!Main.primaryStage.isMaximized())
                 Main.primaryStage.setMaximized(true);
            else
                Main.primaryStage.setMaximized(false);
        });

        // result titles as clickListener
        for (Doc docs:Query.list) {
            docs.getM_title().setOnMouseClicked(e -> {
                System.out.println(docs.getM_title().getText());
                setNode(showPane);
            });
        eventShowPane();

        }
    }

    private void setNode(BorderPane node1) {
        BorderPane node = new BorderPane(node1);
        holdPane.getChildren().clear();
        holdPane.setTop(node.getTop());
        holdPane.setBottom(node.getBottom());
        holdPane.setLeft(node.getLeft());
        holdPane.setRight(node.getRight());
        holdPane.setCenter(node.getCenter());

//        FadeTransition ft = new FadeTransition(Duration.millis(1500));
//        ft.setNode(node);
//        ft.setFromValue(0.1);
//        ft.setToValue(1);
//        ft.setCycleCount(1);
//        ft.setAutoReverse(false);
//        ft.play();
    }

    void eventSearchPane(){
        HBox hBox= (HBox) searchPane.getCenter();
        AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(0);
        Button btn = (Button) anchorPane.getChildren().get(1);
        TextField input = (TextField) anchorPane.getChildren().get(0);
        btn.setOnAction(e ->{
            eventQueryPane(input.getText());
            setNode(queryPane);
        });
    }

    void eventQueryPane(String query){
        VBox hBox= (VBox) queryPane.getTop();
        AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(0);
        TextField input = (TextField) anchorPane.getChildren().get(0);
        input.setText(query);
    }

    void eventShowPane(){
        VBox hBox= (VBox) showPane.getTop();
        VBox vBox = (VBox) hBox.getChildren().get(0);
        JFXButton back = (JFXButton) vBox.getChildren().get(0);
        back.setOnAction(e-> setNode(queryPane));
    }


}
