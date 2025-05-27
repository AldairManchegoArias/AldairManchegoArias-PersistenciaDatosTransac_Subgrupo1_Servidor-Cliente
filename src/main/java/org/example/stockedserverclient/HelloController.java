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

/**
 * Controller class for the chat server's user interface.
 * Manages the UI components and provides methods to update the interface
 * with messages, status updates, and the list of connected users.
 */
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

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up static references to UI components and initializes the status label.
     */
    @FXML
    public void initialize() {
        vboxStatic = vbox_messages;
        labelStatusStatic = labelStatus;
        listViewUsersStatic = listViewUsers;

        labelStatusStatic.setText("Servidor en espera...");
    }

    /**
     * Adds a message to the chat interface.
     * Creates a styled text bubble containing the message and adds it to the specified VBox.
     *
     * @param messageFromClient The message text to display
     * @param vbox The VBox container where the message will be added
     */
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

    /**
     * Updates the status label in the user interface.
     * Uses Platform.runLater to ensure the update happens on the JavaFX application thread.
     *
     * @param message The status message to display
     */
    public static void updateStatus(String message) {
        Platform.runLater(() -> {
            if (labelStatusStatic != null) {
                labelStatusStatic.setText(message);
            }
        });
    }

    /**
     * Updates the list of connected users in the user interface.
     * Replaces the current list with the provided set of usernames.
     * Uses Platform.runLater to ensure the update happens on the JavaFX application thread.
     *
     * @param usernames Set of usernames to display in the list
     */
    public static void updateUserList(Set<String> usernames) {
        Platform.runLater(() -> {
            listViewUsersStatic.getItems().setAll(usernames);
        });
    }
}
