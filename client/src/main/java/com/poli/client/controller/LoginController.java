package com.poli.client.controller;

import com.poli.client.ClientApplication;
import com.poli.client.session.UserSession;
import com.poli.client.socket.SocketClient;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button connectButton;

    @FXML
    private Label statusLabel;

    /** Permite mostrar un mensaje al volver al login (logout, conexión perdida). */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    @FXML
    private void connectToServer() {

        String host = hostField.getText().trim();
        String username = usernameField.getText().trim();

        int port;

        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Puerto inválido");
            return;
        }

        if (host.isBlank() || username.isBlank()) {
            statusLabel.setText("Completa los campos");
            return;
        }

        SocketClient newClient = new SocketClient();

        Task<Response> task = new Task<>() {

            @Override
            protected Response call() throws Exception {

                updateMessage("Conectando...");

                newClient.connect(host, port);

                newClient.send(new Request(MessageType.CONNECT, username, null, null));

                Response response = newClient.receive();

                updateMessage(response.message());

                return response;
            }
        };

        statusLabel.textProperty().bind(task.messageProperty());
        connectButton.setDisable(true);

        task.setOnFailed(event -> {

            statusLabel.textProperty().unbind();
            connectButton.setDisable(false);

            statusLabel.setText("Error: " + task.getException().getMessage());

            disconnectQuietly(newClient);
        });

        task.setOnSucceeded(event -> {

            statusLabel.textProperty().unbind();
            connectButton.setDisable(false);

            Response response = task.getValue();

            if (!response.success()) {
                statusLabel.setText(response.message());
                disconnectQuietly(newClient);
                return;
            }

            openMainScreen(new UserSession(username, newClient));
        });

        Thread thread = new Thread(task, "Login-Task");
        thread.setDaemon(true);
        thread.start();
    }

    private void openMainScreen(UserSession userSession) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();

            // Primero se registran los manejadores de la primera pantalla
            // y luego se empieza a escuchar, para no perder respuestas.
            mainController.setUserSession(userSession);
            userSession.startListening();

            Scene mainScene = new Scene(root, ClientApplication.MAIN_WIDTH, ClientApplication.MAIN_HEIGHT);
            ClientApplication.applyStyles(mainScene);

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setTitle("Chat Socket Poli");
            stage.setScene(mainScene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error al cargar la pantalla principal");
            userSession.closeSession();
        }
    }

    private static void disconnectQuietly(SocketClient client) {
        try {
            client.disconnect();
        } catch (IOException ignored) {
        }
    }
}
