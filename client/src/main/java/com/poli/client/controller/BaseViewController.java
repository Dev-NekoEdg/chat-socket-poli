package com.poli.client.controller;

import com.poli.client.session.UserSession;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import javafx.scene.control.Label;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base para las pantallas que se muestran dentro de main.fxml.
 * Guarda la sesión, el controlador principal (para navegar) y los
 * manejadores de respuestas que la pantalla registró, para limpiarlos
 * cuando se cambia de pantalla.
 */
public abstract class BaseViewController {

    protected MainController main;
    protected UserSession session;

    private final Map<MessageType, Consumer<Response>> handlers = new EnumMap<>(MessageType.class);

    final void init(MainController main, UserSession session) {
        this.main = main;
        this.session = session;
        onInit();
    }

    /** Se ejecuta cuando la pantalla ya tiene sesión y navegación disponibles. */
    protected abstract void onInit();

    /** Registra un manejador para un tipo de respuesta mientras la pantalla esté visible. */
    protected void listen(MessageType type, Consumer<Response> handler) {
        handlers.put(type, handler);
        session.getDispatcher().on(type, handler);
    }

    /** Se llama al salir de la pantalla. */
    void dispose() {
        handlers.forEach((type, handler) -> session.getDispatcher().off(type, handler));
        handlers.clear();
    }

    /**
     * Envía una solicitud al servidor. Si falla, muestra el error en el label indicado.
     * @return true si la solicitud se envió.
     */
    protected boolean sendRequest(MessageType type, String roomId, String content, Label statusLabel) {
        try {
            session.send(type, roomId, content);
            return true;
        } catch (IOException e) {
            showError(statusLabel, "No se pudo enviar la solicitud: " + e.getMessage());
            return false;
        }
    }

    protected static void showError(Label label, String message) {
        label.getStyleClass().remove("status-info");
        if (!label.getStyleClass().contains("status-error")) {
            label.getStyleClass().add("status-error");
        }
        label.setText(message);
    }

    protected static void showInfo(Label label, String message) {
        label.getStyleClass().remove("status-error");
        if (!label.getStyleClass().contains("status-info")) {
            label.getStyleClass().add("status-info");
        }
        label.setText(message);
    }
}
