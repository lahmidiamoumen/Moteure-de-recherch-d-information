package sample.controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import sample.model.Doc;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Query implements Initializable {
    @FXML
    VBox vBox;
    public static List<Doc> list = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Document document = new Document("Moumen","Moumen Diouri, né le 20 février 1938 à Kénitra et mort le 16 mai 2012 (à 74 ans) à Rabat, est un des rares opposants exilés du Maroc à avoir prôné une ...");
//        document.getM_title().setOnMouseClicked(event -> System.out.println("label clicked"));
//        vBox.getChildren().add(document.getM_title());
//        vBox.getChildren().add(document.getM_abstract());
        createContent(vBox);

    }

    public void createContent(VBox vBox) {

        for (int i = 0; i < 12; i++) {
            list.add(new Doc("Title " + i, "Contents lahmid mouem kan ya makane wal ofi " + i));
        }

        JFXListView<Doc> listView = new JFXListView<>();
        listView.setBorder(Border.EMPTY);
//        listView.setMaxHeight(Double.MAX_VALUE);
//        listView.setMouseTransparent( true );
        listView.setExpanded(true);
        listView.setFocusTraversable( false );
        ObservableList<Doc> myObservableList = FXCollections.observableList(list);
        listView.setItems(myObservableList);

        vBox.getChildren().add(listView);

    }
}
