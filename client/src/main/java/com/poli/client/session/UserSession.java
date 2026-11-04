package com.poli.client.session;

import com.poli.client.socket.SocketClient;

public class UserSession {
    // private String userId;
    private String username;
    private SocketClient socketClient;

    public UserSession(
            // String userId,
            String username,
            SocketClient socketClient) {

        //this.userId = userId;
        this.username = username;
        this.socketClient = socketClient;
    }

    // public String getUserId() {
    //     return userId;
    // }

    public String getUsername() {
        return username;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

     public void closeSession() {
        try {
            socketClient.disconnect();
        } catch (Exception e) {
            System.err.println(
                    "Error al cerrar la sesión: " + e.getMessage());
        }
    }
}
