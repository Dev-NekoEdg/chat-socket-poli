package com.poli.server.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;
import com.poli.common.enums.MessageType;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            PrintWriter writer = new PrintWriter(
                socket.getOutputStream(), true
            )
        ) {

            String json;

            while ((json = reader.readLine()) != null) {

                Request request = mapper.readValue(
                    json,
                    Request.class
                );

                System.out.println(
                    "Solicitud recibida: " + request.type()
                );

                Response response = new Response(
                    true,
                    MessageType.CONNECTED,
                    "Solicitud recibida correctamente",
                    null
                );

                writer.println(
                    mapper.writeValueAsString(response)
                );
            }

        } catch (IOException e) {

            System.out.println(
                "Cliente desconectado: " + socket.getRemoteSocketAddress()
            );

        } finally {

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}