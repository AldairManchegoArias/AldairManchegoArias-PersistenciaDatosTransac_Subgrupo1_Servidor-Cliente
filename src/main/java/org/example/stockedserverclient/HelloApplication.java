package org.example.stockedserverclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class that initializes the chat server GUI.
 * Extends JavaFX Application to provide a graphical interface for the server.
 */
public class HelloApplication extends Application {

    /**
     * Initializes the JavaFX application.
     * Sets up the main window, loads the FXML layout, and starts the server in a background thread.
     *
     * @param stage The primary stage for this application
     * @throws Exception If an error occurs during initialization
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);
        stage.setTitle("Servidor de Chat");
        stage.setScene(scene);
        stage.show();

        // Inicia el servidor en segundo plano
        new Thread(() -> {
            try {
                ServerSocketLauncher.start(); // clase para encapsular el arranque
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * The main entry point for the application.
     * Launches the JavaFX application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
