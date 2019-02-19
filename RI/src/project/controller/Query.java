package project.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import project.model.DocFx;
import project.model.Querying;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Query implements Initializable {
    @FXML
    HBox vBox;
    static  Thread th = null;

    static Boolean show = false;

    @FXML
    Button btn;

    static JFXSpinner spinner;
    public static  JFXListView<DocFx> listView;

    @FXML
    TextField textField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createContent();
    }

    public void createContent() {

        listView = new JFXListView<>();
        listView.setBorder(Border.EMPTY);
        listView.setExpanded(true);
        listView.setFocusTraversable( false );
        listView.setPadding(new Insets(0,5,0,0));
        listView.setPrefWidth(1000);

        spinner = new JFXSpinner();
    }

    public static void querying(String str, HBox vBox){

        listView.getItems().clear();

        LoadFriendsTask loadFriendsTask = new LoadFriendsTask(str);
        loadFriendsTask.setOnSucceeded(e->{
            Home.bool = false;
            vBox.setAlignment(Pos.CENTER_LEFT);
            vBox.getChildren().remove(spinner);
            Home.pagination.setPageCount(listView.getItems().size()/10);
            vBox.getChildren().add(Home.pagination);
        });
        if(th != null) th.stop();
        th =new Thread(loadFriendsTask);
        th.start();
        vBox.getChildren().clear();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(spinner);
    }

    protected static class LoadFriendsTask extends Task<List<DocFx>>
    {
        String query;
        public LoadFriendsTask(String query) {
            this.query = query;
        }
        @Override
        protected List<DocFx> call() {
            List<DocFx> result = new Querying(query).getDocFx();
            return result;
        }
        @Override
        protected void succeeded() {
            listView.getItems().setAll(getValue());
        }

    }
}
