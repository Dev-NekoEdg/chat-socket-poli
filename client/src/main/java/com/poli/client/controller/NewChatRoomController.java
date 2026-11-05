package com.poli.client.controller;

import com.poli.common.dto.ChatRoom;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;
import com.poli.common.util.JsonUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Formulario para crear un chatroom nuevo. */
public class NewChatRoomController extends BaseViewController {

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox privateCheck;

    @FXML
    private ListView<String> usersList;

    @FXML
    private Label selectedCountLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button createButton;

    /** Estado (seleccionado o no) de cada usuario de la lista. */
    private final Map<String, BooleanProperty> selection = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        usersList.setCellFactory(CheckBoxListCell.forListView(this::selectionOf));
        usersList.setPlaceholder(new Label("Cargando usuarios..."));
        updateSelectedCount();
    }

    @Override
    protected void onInit() {
        listen(MessageType.LIST_USERS, this::onUsersReceived);
        listen(MessageType.CREATE_ROOM, this::onRoomCreated);
        listen(MessageType.ERROR, response -> {
            createButton.setDisable(false);
            showError(statusLabel, response.message());
        });

        sendRequest(MessageType.LIST_USERS, null, null, statusLabel);
    }

    private BooleanProperty selectionOf(String username) {
        return selection.computeIfAbsent(username, key -> {
            BooleanProperty property = new SimpleBooleanProperty(false);
            property.addListener((obs, oldValue, newValue) -> updateSelectedCount());
            return property;
        });
    }

    private void updateSelectedCount() {
        long count = selection.values().stream().filter(BooleanProperty::get).count();
        selectedCountLabel.setText(count + " seleccionado(s)");
    }

    private List<String> selectedUsers() {
        List<String> users = new ArrayList<>();
        selection.forEach((user, selected) -> {
            if (selected.get()) {
                users.add(user);
            }
        });
        return users;
    }

    private void onUsersReceived(Response response) {

        if (!response.success()) {
            showError(statusLabel, response.message());
            return;
        }

        try {
            List<String> users = JsonUtil.fromJsonList(response.data(), String.class)
                    .stream()
                    // El usuario actual siempre es participante; no se lista.
                    .filter(user -> !user.equalsIgnoreCase(session.getUsername()))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

            usersList.getItems().setAll(users);
            usersList.setPlaceholder(new Label("No hay otros usuarios disponibles."));

        } catch (UncheckedIOException e) {
            showError(statusLabel, "Respuesta inválida del servidor");
        }
    }

    @FXML
    private void handleCreate() {

        String name = nameField.getText().trim();
        List<String> members = selectedUsers();

        if (name.isBlank()) {
            showError(statusLabel, "El nombre del chatroom es obligatorio");
            nameField.requestFocus();
            return;
        }

        if (members.isEmpty()) {
            showError(statusLabel, "Selecciona al menos un miembro");
            return;
        }

        List<String> participants = new ArrayList<>();
        participants.add(session.getUsername());
        participants.addAll(members);

        ChatRoom room = new ChatRoom(null, name, privateCheck.isSelected(), participants);

        createButton.setDisable(true);
        showInfo(statusLabel, "Creando chatroom...");

        if (!sendRequest(MessageType.CREATE_ROOM, null, JsonUtil.toJson(room), statusLabel)) {
            createButton.setDisable(false);
        }
    }

    private void onRoomCreated(Response response) {

        createButton.setDisable(false);

        if (!response.success()) {
            showError(statusLabel, response.message());
            return;
        }

        main.navigateToChatRoomList();
    }

    @FXML
    private void handleCancel() {
        main.navigateToChatRoomList();
    }
}
