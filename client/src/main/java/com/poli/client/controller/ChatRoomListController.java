package com.poli.client.controller;

import com.poli.common.dto.ChatRoom;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;
import com.poli.common.util.JsonUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;

/** Lista de las conversaciones del usuario. */
public class ChatRoomListController extends BaseViewController {

    @FXML
    private ListView<ChatRoom> roomList;

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        roomList.setCellFactory(list -> new ChatRoomCell());
        roomList.setPlaceholder(new Label("No tienes conversaciones todavía.\nCrea una con \"+ Nuevo chatroom\"."));
    }

    @Override
    protected void onInit() {
        listen(MessageType.LIST_ROOMS, this::onRoomsReceived);

        // Si otro usuario nos agrega a una sala nueva, se refresca la lista.
        listen(MessageType.CREATE_ROOM, response -> requestRooms());

        listen(MessageType.ERROR, response -> showError(statusLabel, response.message()));

        requestRooms();
    }

    @FXML
    private void requestRooms() {
        showInfo(statusLabel, "Cargando conversaciones...");
        sendRequest(MessageType.LIST_ROOMS, null, null, statusLabel);
    }

    @FXML
    private void handleNewChatRoom() {
        main.navigateToNewChatRoom();
    }

    private void onRoomsReceived(Response response) {

        if (!response.success()) {
            showError(statusLabel, response.message());
            return;
        }

        try {
            List<ChatRoom> rooms = JsonUtil.fromJsonList(response.data(), ChatRoom.class)
                    .stream()
                    .sorted(Comparator.comparing(ChatRoom::name, String.CASE_INSENSITIVE_ORDER))
                    .toList();

            roomList.getItems().setAll(rooms);
            statusLabel.setText("");

        } catch (UncheckedIOException e) {
            showError(statusLabel, "Respuesta inválida del servidor");
        }
    }

    /** Celda: nombre de la sala, privada/pública y número de participantes. */
    private class ChatRoomCell extends ListCell<ChatRoom> {

        private final Label nameLabel = new Label();
        private final Label typeLabel = new Label();
        private final Label participantsLabel = new Label();
        private final HBox container;

        ChatRoomCell() {
            nameLabel.getStyleClass().add("room-name");
            typeLabel.getStyleClass().add("room-tag");
            participantsLabel.getStyleClass().add("muted");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox info = new VBox(4, nameLabel, typeLabel);
            container = new HBox(10, info, spacer, participantsLabel);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("room-cell");

            setOnMouseClicked(event -> {
                if (!isEmpty() && getItem() != null) {
                    main.navigateToChatRoom(getItem());
                }
            });
        }

        @Override
        protected void updateItem(ChatRoom room, boolean empty) {
            super.updateItem(room, empty);

            if (empty || room == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            nameLabel.setText(room.name());

            typeLabel.setText(room.privateRoom() ? "Privado" : "Público");
            typeLabel.getStyleClass().removeAll("room-tag-private", "room-tag-public");
            typeLabel.getStyleClass().add(room.privateRoom() ? "room-tag-private" : "room-tag-public");

            int count = room.participantCount();
            participantsLabel.setText(count + (count == 1 ? " participante" : " participantes"));

            setText(null);
            setGraphic(container);
        }
    }
}
