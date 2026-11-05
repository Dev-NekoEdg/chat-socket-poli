package com.poli.client.session;

import com.poli.client.socket.ResponseDispatcher;
import com.poli.client.socket.SocketClient;
import com.poli.common.dto.Request;
import com.poli.common.enums.MessageType;

import java.io.IOException;

public class UserSession {

    private final String username;
    private final SocketClient socketClient;
    private final ResponseDispatcher dispatcher = new ResponseDispatcher();

    public UserSession(String username, SocketClient socketClient) {
        this.username = username;
        this.socketClient = socketClient;
    }

    public String getUsername() {
        return username;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public ResponseDispatcher getDispatcher() {
        return dispatcher;
    }

    /** Comienza a escuchar al servidor y a repartir sus respuestas. */
    public void startListening() {
        socketClient.startListening(dispatcher);
    }

    /** Envía una solicitud a nombre del usuario de la sesión. */
    public void send(MessageType type, String roomId, String content) throws IOException {
        socketClient.send(new Request(type, username, roomId, content));
    }

    public void closeSession() {
        try {
            // Avisar al servidor; si falla, igual se cierra el socket.
            send(MessageType.DISCONNECT, null, null);
        } catch (IOException ignored) {
        }

        try {
            socketClient.disconnect();
        } catch (IOException e) {
            System.err.println("Error al cerrar la sesión: " + e.getMessage());
        }
    }
}
