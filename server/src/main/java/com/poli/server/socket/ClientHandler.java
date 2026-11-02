package com.poli.server.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper;

    private PrintWriter writer;
    private String userId;
    // Por defecto, el usuario debe estas en false hasta que se registre
    // correctamente.
    private boolean registered = false;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        // seguro para manejar múltiples clientes simultáneamente.
        registered = false;
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));) {

            writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), 
                    StandardCharsets.UTF_8),
                    true);
            String json;

            while ((json = reader.readLine()) != null) {

                Request request = mapper.readValue(
                        json,
                        Request.class);

                System.out.println(
                        "Solicitud recibida: " + request.type());

                switch (request.type()) {

                    case CONNECT:
                        // Registrar usuario
                        if (!handleConnect(request)) {
                            return;
                        }
                        break;

                    case CHAT:
                        // Procesar mensaje
                        handleChat(request);
                        break;

                    case DISCONNECT:
                        // Desconectar usuario
                        handleDisconnect(request);
                        break;

                    default:
                        // Manejar operación desconocida
                        handleDefault(request);

                        break;
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Cliente desconectado: " + socket.getRemoteSocketAddress());

        } finally {

            if (registered && userId != null) {

                ConnectedUsers.remove(userId, this);

                System.out.println(
                    "Usuario removido: " + userId
                );
            }

            try {
                socket.close();
            } catch (IOException ignored) { }
        }
    }

    private boolean handleConnect(Request request) throws IOException {

        if (registered) {

            send(new Response(
                false,
                MessageType.ERROR,
                "El cliente ya está conectado",
                null
            ));

            return false;
        }

        userId = request.userId();
        if (userId == null || userId.isBlank()) {
            
            send(new Response(
                false,
                MessageType.ERROR,
                "Username inválido",
                null));
                
                return false;
            }
            
        registered = ConnectedUsers.add(userId, this);

        if (!registered) {

            send(new Response(
                    false,
                    MessageType.ERROR,
                    "El usuario ya está conectado",
                    null));

            return false;
        }

        send(new Response(
                true,
                MessageType.CONNECTED,
                "Conexión establecida correctamente",
                null));

        System.out.println(
                "Usuario conectado: " + userId);
        return registered;

    }

    private void handleDisconnect(Request request) throws IOException {
        send(new Response(
                true,
                MessageType.DISCONNECT,
                "Desconexión solicitada",
                null));
    }

    private void handleChat(Request request) throws IOException {
        // Implementar lógica para manejar mensajes de chat
    }

    private void handleDefault(Request request) throws IOException {
        send(new Response(
                false,
                MessageType.ERROR,
                "Tipo de solicitud no soportado",
                null));
    }

    public synchronized void send(Response response)
            throws IOException {

        String json = mapper.writeValueAsString(response);

        writer.println(json);

        if (writer.checkError()) {
            throw new IOException("Error enviando respuesta");
        }
    }

}