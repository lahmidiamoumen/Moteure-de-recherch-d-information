package sample.model;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class Doc extends VBox {
    Label m_title = new Label();
    Label m_abstract = new Label();

    public Doc(String m_title, String m_abstract){
        super();
        this.m_title.setText(m_title);
        this.m_abstract.setText(m_abstract);
        this.m_title.setId("label_head");
        this.m_abstract.setId("label_content");

        this.getChildren().addAll(this.m_title,this.m_abstract);
    }

    public Label getM_title() {
        return m_title;
    }

    public Label getM_abstract() {
        return m_abstract;
    }
}
