package com.poli.client.controller;

import com.poli.client.session.UserSession;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane contentPane;

    @FXML
    private Label usernameLabel;

    private UserSession userSession;

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;

        usernameLabel.setText(
                "Usuario: " + userSession.getUsername());
    }

    public UserSession getUserSession() {
        return userSession;
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath));

            Parent view = loader.load();

            contentPane.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();

            usernameLabel.setText(
                    "Error al cargar la pantalla");
        }
    }

    public void navigateToChatRoomList() {
        loadView("/fxml/chatroomList.fxml");
    }

    public void navigateToNewChatRoom() {
        loadView("/fxml/newChatroom.fxml");
    }

    public void navigateToChatRoom(String roomId) {
        loadView("/fxml/chatroom.fxml");
    }

    @FXML
    private void handleLogout() {
        if (userSession != null) {
            userSession.closeSession();
        }

        // Próximo paso: regresar al Login.
    }
}
