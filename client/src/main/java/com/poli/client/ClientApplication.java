package com.poli.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    public static final double LOGIN_WIDTH = 450;
    public static final double LOGIN_HEIGHT = 350;

    public static final double MAIN_WIDTH = 900;
    public static final double MAIN_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);
        applyStyles(scene);

        stage.setTitle("Chat Socket Poli");
        stage.setScene(scene);
        stage.show();
    }

    /** Aplica la hoja de estilos común a una escena. */
    public static void applyStyles(Scene scene) {
        scene.getStylesheets().add(
            ClientApplication.class.getResource("/css/app.css").toExternalForm()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
