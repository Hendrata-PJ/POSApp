package com.minipos;

import com.minipos.util.DatabaseHelper;
import com.minipos.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize Database
        DatabaseHelper.initializeDatabase();

        scene = new Scene(loadFXML("login"), 800, 600);
        scene.getStylesheets().add(App.class.getResource("/styles/style.css").toExternalForm());

        stage.setTitle("MiniPOS - Modern Point of Sale");
        stage.setScene(scene);
        stage.setFullScreen(true); // True fullscreen (borderless)
        stage.setFullScreenExitHint(""); // Hide the "Press ESC to exit fullscreen" message
        stage.show();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
