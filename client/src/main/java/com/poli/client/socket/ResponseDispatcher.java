package com.poli.client.socket;

import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import javafx.application.Platform;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Recibe todas las respuestas del servidor (desde el hilo de escucha) y las
 * entrega, en el hilo de JavaFX, al manejador registrado para su MessageType.
 *
 * Cada pantalla registra sus manejadores al abrirse y los elimina al cerrarse.
 */
public class ResponseDispatcher implements IResponseListener {

    private final Map<MessageType, Consumer<Response>> handlers = new ConcurrentHashMap<>();

    private volatile Consumer<IOException> connectionLostHandler;

    public void on(MessageType type, Consumer<Response> handler) {
        handlers.put(type, handler);
    }

    /** Elimina el manejador solo si sigue siendo el mismo que se registró. */
    public void off(MessageType type, Consumer<Response> handler) {
        handlers.remove(type, handler);
    }

    public void onConnectionLost(Consumer<IOException> handler) {
        this.connectionLostHandler = handler;
    }

    @Override
    public void onResponse(Response response) {

        Consumer<Response> handler = handlers.get(response.type());

        if (handler == null) {
            System.out.println(
                    "Respuesta sin manejador (" + response.type() + "): " + response.message());
            return;
        }

        Platform.runLater(() -> handler.accept(response));
    }

    @Override
    public void onError(IOException e) {

        Consumer<IOException> handler = connectionLostHandler;

        if (handler != null) {
            Platform.runLater(() -> handler.accept(e));
        }
    }
}
