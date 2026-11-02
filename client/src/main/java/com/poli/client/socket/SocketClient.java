package com.poli.client.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private final ObjectMapper mapper = new ObjectMapper();

    public void connect(String host, int port) throws IOException {

        socket = new Socket(host, port);

        reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );

        writer = new PrintWriter(
            socket.getOutputStream(), true
        );

        System.out.println("Conectado al servidor.");
    }

    public void send(Request request) throws IOException {

        String json = mapper.writeValueAsString(request);

        writer.println(json);

        if (writer.checkError()) {
            throw new IOException("No se pudo enviar la solicitud");
        }
    }

    public Response receive() throws IOException {

        String json = reader.readLine();

        if (json == null) {
            throw new IOException("El servidor cerró la conexión");
        }

        return mapper.readValue(
            json,
            Response.class
        );
    }

    public void disconnect() throws IOException {

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}