package com.poli.client.controller;

import com.poli.client.socket.SocketClient;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

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

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                SocketClient client = new SocketClient();

                client.connect(host, port);

                Request request = new Request(
                    MessageType.CONNECT,
                    username,
                    null,
                    null
                );

                client.send(request);

                Response response = client.receive();

                updateMessage(response.message());

                client.disconnect();

                return null;
            }
        };

        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnFailed(event -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText(
                "Error: " + task.getException().getMessage()
            );
        });

        task.setOnSucceeded(event -> {
            statusLabel.textProperty().unbind();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}