package pl.edu.mini.pw.zpoif.projectCurrencies.app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.BatchUpdateException;
import java.util.Scanner;

public class AlertBox {

    public static void  display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Zamknij");
        closeButton.setOnAction(e -> window.close());

        VBox layaout = new VBox(10);
        layaout.getChildren().addAll(label, closeButton);
        layaout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layaout);
        window.setScene(scene);
        window.showAndWait();
    }
}
