package com.poli.client.controller;

import com.poli.client.ClientApplication;
import com.poli.client.session.UserSession;
import com.poli.common.dto.ChatRoom;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contenedor principal: la barra superior (usuario + cerrar sesión) siempre
 * está visible y el centro cambia entre chatroomList, newChatroom y chatroom.
 */
public class MainController {

    @FXML
    private BorderPane contentPane;

    @FXML
    private Label usernameLabel;

    private UserSession userSession;

    private BaseViewController currentView;

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;

        usernameLabel.setText("Usuario: " + userSession.getUsername());

        userSession.getDispatcher().onConnectionLost(
                e -> returnToLogin("Conexión perdida: " + e.getMessage()));

        navigateToChatRoomList();
    }

    public UserSession getUserSession() {
        return userSession;
    }

    // #region Navegación

    public void navigateToChatRoomList() {
        loadView("/fxml/chatroomList.fxml");
    }

    public void navigateToNewChatRoom() {
        loadView("/fxml/newChatroom.fxml");
    }

    public void navigateToChatRoom(ChatRoom room) {
        ChatRoomController controller = loadView("/fxml/chatroom.fxml");
        if (controller != null) {
            controller.openRoom(room);
        }
    }

    // #endregion

    private <T extends BaseViewController> T loadView(String fxmlPath) {

        if (currentView != null) {
            currentView.dispose();
            currentView = null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            T controller = loader.getController();
            controller.init(this, userSession);

            currentView = controller;
            contentPane.setCenter(view);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            contentPane.setCenter(new Label("Error al cargar la pantalla: " + fxmlPath));
            return null;
        }
    }

    @FXML
    private void handleLogout() {

        if (currentView != null) {
            currentView.dispose();
            currentView = null;
        }

        if (userSession != null) {
            userSession.getDispatcher().onConnectionLost(null);
            userSession.closeSession();
        }

        returnToLogin("Sesión cerrada");
    }

    private void returnToLogin(String statusMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setStatus(statusMessage);

            Scene scene = new Scene(root, ClientApplication.LOGIN_WIDTH, ClientApplication.LOGIN_HEIGHT);
            ClientApplication.applyStyles(scene);

            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
