package project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import project.Main;
import project.model.DocFx;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Home implements Initializable {
    @FXML
    Button close,minimise,maximise;
    @FXML
    public  BorderPane holdPane;
    public static BorderPane searchPane,queryPane,showPane;
    static  Pagination pagination = null;


    public static Boolean bool = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../view/query.fxml"));
        try {
            queryPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Query c = fxmlLoader.getController();
        try {
            searchPane = FXMLLoader.load(getClass().getResource("../view/search.fxml"));
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
        c.btn.setOnAction(e-> {
            if(!Home.bool){
                action(c .textField.getText());Home.bool = true;
            }
        });
        c.textField.setOnKeyPressed(ke -> {
            if(!Home.bool){
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    action(c.textField.getText());
                    Home.bool = true;
                }
            }
        });
        eventShowPane();
    }

     public void setNode(BorderPane node1) {
        BorderPane node = new BorderPane(node1);
        holdPane.getChildren().clear();
        holdPane.setTop(node.getTop());
        holdPane.setBottom(node.getBottom());
        holdPane.setLeft(node.getLeft());
        holdPane.setRight(node.getRight());
        holdPane.setCenter(node.getCenter());
    }

     void eventSearchPane(){
        HBox hBox= (HBox) searchPane.getCenter();
        AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(0);
        Button btn = (Button) anchorPane.getChildren().get(1);
        TextField input = (TextField) anchorPane.getChildren().get(0);
        input.setOnKeyPressed(ke -> { if (ke.getCode().equals(KeyCode.ENTER) && !input.getText().isEmpty() && !input.getText().matches("[ ]+")) {action(input.getText()); } });
        btn.setOnAction(e ->{
            if(!input.getText().isEmpty() && !input.getText().matches("[ ]+"))
                action(input.getText());});
    }

     private void action(String input) {
        if(!bool){
            pagination = new Pagination();
            pagination.setPageFactory((Integer es) -> createPage(es));
            eventQueryPane(input);
            bool = true;
            setNode(queryPane);
        }
    }

    static void eventQueryPane(String query){
        VBox hBox= (VBox) queryPane.getTop();
        AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(0);
        TextField input = (TextField) anchorPane.getChildren().get(0);
        input.setText(query);
        Query.querying(query, (HBox) queryPane.getCenter());
    }

    void eventShowPane(){
        VBox hBox= (VBox) showPane.getTop();
        VBox vBox = (VBox) hBox.getChildren().get(0);
        JFXButton back = (JFXButton) vBox.getChildren().get(0);
        back.setOnAction(e-> setNode(queryPane));
    }
     public static void eventShow(String titre,String abstrac){
        VBox vBo = (VBox) showPane.getTop();
        Label label = (Label) vBo.getChildren().get(1);
        label.setText(titre);
        BorderPane vBox = (BorderPane) showPane.getCenter();
        TextArea textArea = (TextArea) vBox.getTop();
        textArea.setEditable(false);
        textArea.setText(abstrac.replaceAll("^","\n "));

    }

    public static int itemsPerPage() {
        return 10;
    }

    public  ListView<DocFx> createPage(int pageIndex) {
        JFXListView<DocFx> vBox = new JFXListView<>();
        vBox.setCellFactory(l -> new JFXListCell<DocFx>() {
            { setPrefWidth(0);} // avoids the issues
            @Override
            public void updateItem(DocFx item, boolean empty) {
                super.updateItem(item, empty);
            }
        });
        vBox.setExpanded(true);
        vBox.setBorder(Border.EMPTY);
        vBox.setFocusTraversable( false );
        vBox.setPadding(new Insets(0,5,0,0));
        vBox.setPrefWidth(Double.valueOf(1000));
        int page = pageIndex * itemsPerPage();
        int a = Query.listView.getItems().size();
        int b = 0;
        int count = a/10;
        if ( a % 10 != 0){
            b =1;
            int s = a % 10;
            if(pageIndex == count){
                page = pageIndex * s;
                for (int i = page; i < page + s; i++) {
                    DocFx docFx = Query.listView.getItems().get(i);
                    vBox.getItems().add(docFx);
                }
            }
            else {
                for (int i = page; i < page + itemsPerPage(); i++) {
                    DocFx docFx = Query.listView.getItems().get(i);
                    vBox.getItems().add(docFx);
                }
            }
        }
        else {
            for (int i = page; i < page + itemsPerPage(); i++) {
                DocFx docFx = Query.listView.getItems().get(i);
                vBox.getItems().add(docFx);
            }
        }

        if(pageIndex == 0){
            pagination.setPageCount(Query.listView.getItems().size()/10+b);
        }
        vBox.setOnMouseClicked(ev->{
            DocFx docFx = vBox.getSelectionModel().getSelectedItem();
            eventShow(docFx.getFullTile(),docFx.getFullAbstract()); setNode(showPane);
        });
        return vBox;
    }
}
