package org.example.stockedserverclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Set;

public class HelloController {

    @FXML
    private VBox vbox_messages;

    @FXML
    private Label labelStatus;

    @FXML
    private ListView<String> listViewUsers;

    public static VBox vboxStatic;
    public static Label labelStatusStatic;
    public static ListView<String> listViewUsersStatic;

    @FXML
    public void initialize() {
        vboxStatic = vbox_messages;
        labelStatusStatic = labelStatus;
        listViewUsersStatic = listViewUsers;

        labelStatusStatic.setText("Servidor en espera...");
    }

    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,236); -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        hBox.getChildren().add(textFlow);

        Platform.runLater(() -> vbox.getChildren().add(hBox));
    }

    public static void updateStatus(String message) {
        Platform.runLater(() -> {
            if (labelStatusStatic != null) {
                labelStatusStatic.setText(message);
            }
        });
    }

    public static void updateUserList(Set<String> usernames) {
        Platform.runLater(() -> {
            listViewUsersStatic.getItems().setAll(usernames);
        });
    }
}
