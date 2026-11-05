package com.poli.client.controller;

import com.poli.common.dto.ChatMessage;
import com.poli.common.dto.ChatRoom;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;
import com.poli.common.util.JsonUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Conversación de un chatroom. */
public class ChatRoomController extends BaseViewController {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    private Label roomNameLabel;

    @FXML
    private Label participantsLabel;

    @FXML
    private ListView<ChatMessage> messageList;

    @FXML
    private TextArea messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private Label statusLabel;

    private ChatRoom room;

    @FXML
    private void initialize() {
        messageList.setCellFactory(list -> new MessageCell());
        messageList.setPlaceholder(new Label("Aún no hay mensajes. ¡Escribe el primero!"));
        messageList.setFocusTraversable(false);

        // Enter envía; Shift+Enter agrega un salto de línea.
        messageInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (event.isShiftDown()) {
                    messageInput.insertText(messageInput.getCaretPosition(), "\n");
                } else {
                    handleSend();
                }
            }
        });

        sendButton.disableProperty().bind(messageInput.textProperty().isEmpty());
    }

    @Override
    protected void onInit() {
        listen(MessageType.CHAT_HISTORY, this::onHistoryReceived);
        listen(MessageType.CHAT, this::onMessageReceived);
        listen(MessageType.ERROR, response -> showError(statusLabel, response.message()));
    }

    /** Muestra la sala indicada y pide su historial de mensajes. */
    public void openRoom(ChatRoom room) {
        this.room = room;

        roomNameLabel.setText(room.name() + (room.privateRoom() ? "  (Privado)" : "  (Público)"));
        participantsLabel.setText("Participantes: " + String.join(", ", room.participants()));

        sendRequest(MessageType.CHAT_HISTORY, room.id(), null, statusLabel);

        messageInput.requestFocus();
    }

    @FXML
    private void handleSend() {

        String text = messageInput.getText().strip();

        if (text.isEmpty() || room == null) {
            return;
        }

        // El mensaje se agrega a la lista cuando el servidor lo reenvía (CHAT),
        // así todos los participantes lo ven en el mismo orden.
        if (sendRequest(MessageType.CHAT, room.id(), text, statusLabel)) {
            messageInput.clear();
            statusLabel.setText("");
        }
    }

    @FXML
    private void handleBack() {
        main.navigateToChatRoomList();
    }

    private void onHistoryReceived(Response response) {

        if (!response.success()) {
            showError(statusLabel, response.message());
            return;
        }

        try {
            List<ChatMessage> messages = JsonUtil.fromJsonList(response.data(), ChatMessage.class)
                    .stream()
                    .filter(this::belongsToRoom)
                    .sorted(Comparator.comparing(
                            ChatMessage::createDate,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .toList();

            messageList.getItems().setAll(messages);
            scrollToBottom();

        } catch (UncheckedIOException e) {
            showError(statusLabel, "Historial inválido recibido del servidor");
        }
    }

    private void onMessageReceived(Response response) {

        if (!response.success()) {
            showError(statusLabel, response.message());
            return;
        }

        try {
            ChatMessage message = JsonUtil.fromJson(response.data(), ChatMessage.class);

            // Puede llegar un mensaje de otra sala; solo se muestra si es de esta.
            if (belongsToRoom(message)) {
                messageList.getItems().add(message);
                scrollToBottom();
            }

        } catch (UncheckedIOException | IllegalArgumentException e) {
            showError(statusLabel, "Mensaje inválido recibido del servidor");
        }
    }

    private boolean belongsToRoom(ChatMessage message) {
        return message != null && room != null && Objects.equals(message.roomId(), room.id());
    }

    private void scrollToBottom() {
        if (!messageList.getItems().isEmpty()) {
            messageList.scrollTo(messageList.getItems().size() - 1);
        }
    }

    /** Celda de mensaje: los propios a la derecha, los demás a la izquierda. */
    private class MessageCell extends ListCell<ChatMessage> {

        private final Label authorLabel = new Label();
        private final Label contentLabel = new Label();
        private final VBox bubble = new VBox(3, authorLabel, contentLabel);
        private final HBox row = new HBox(bubble);

        MessageCell() {
            authorLabel.getStyleClass().add("message-author");
            contentLabel.getStyleClass().add("message-content");
            contentLabel.setWrapText(true);
            bubble.getStyleClass().add("message-bubble");
            bubble.maxWidthProperty().bind(messageList.widthProperty().multiply(0.7));

            getStyleClass().add("message-cell");
        }

        @Override
        protected void updateItem(ChatMessage message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            boolean own = message.userId() != null
                    && message.userId().equalsIgnoreCase(session.getUsername());

            String time = message.createDate() == null ? "" : " · " + TIME_FORMAT.format(message.createDate());
            authorLabel.setText((own ? "Tú" : message.userId()) + time);
            contentLabel.setText(message.content());

            bubble.getStyleClass().removeAll("message-own", "message-other");
            bubble.getStyleClass().add(own ? "message-own" : "message-other");
            row.setAlignment(own ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            setText(null);
            setGraphic(row);
        }
    }
}
