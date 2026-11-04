package com.poli.client.controller;

import com.poli.client.session.UserSession;
import com.poli.client.socket.IResponseListener;
import com.poli.client.socket.SocketClient;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;

public class LoginController {

    private SocketClient client;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label statusLabel;

    @FXML
    private void connectToServer() {

        String host = hostField.getText();
        String username = usernameField.getText();

        int port;

        try {
            port = Integer.parseInt(portField.getText());
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

                newClient.connect(host, port);

                Request request = new Request(
                        MessageType.CONNECT,
                        username,
                        null,
                        null);

                newClient.send(request);

                Response response = newClient.receive();

                updateMessage(response.message());

                return response;
            }
        };

        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnFailed(event -> {

            statusLabel.textProperty().unbind();

            statusLabel.setText(
                    "Error: " + task.getException().getMessage());

            try {
                newClient.disconnect();
            } catch (IOException ignored) {
            }
        });

        task.setOnSucceeded(event -> {

            statusLabel.textProperty().unbind();

            Response response = task.getValue();

            if (!response.success()) {

                statusLabel.setText(response.message());

                try {
                    newClient.disconnect();
                } catch (IOException ignored) {
                }

                return;
            }
            client = newClient;

            // 1. Crear la sesión del usuario
            UserSession userSession = new UserSession(
                    username,
                    newClient);

            // 2. Iniciar el listener del socket
            client.startListening(
                    new IResponseListener() {

                        @Override
                        public void onResponse(Response response) {
                            Platform.runLater(() -> {
                                System.out.println(
                                        "Respuesta recibida: "
                                                + response.message());
                            });
                        }

                        @Override
                        public void onError(IOException exception) {
                            Platform.runLater(() -> {
                                statusLabel.setText(
                                        "Conexión perdida: "
                                                + exception.getMessage());
                            });
                        }
                    });

            statusLabel.setText(
                    "Conexión establecida correctamente");

            try {

                // 3. Cargar main.fxml
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/main.fxml"));

                Parent root = loader.load();

                // 4. Obtener el MainController
                MainController mainController = loader.getController();

                // 5. Entregar la sesión
                mainController.setUserSession(userSession);

                // 6. Cambiar la escena actual
                Scene mainScene = new Scene(root, 900, 600);

                Stage stage = (Stage) statusLabel
                        .getScene()
                        .getWindow();

                stage.setTitle("Chat Socket Poli");
                stage.setScene(mainScene);
                stage.centerOnScreen();

            } catch (IOException e) {

                e.printStackTrace();

                statusLabel.setText(
                        "Error al cargar la pantalla principal");
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}