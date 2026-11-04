package com.poli.client.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.common.dto.Request;
import com.poli.common.dto.Response;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
 
    // Hilo para escuchar las respuestas del servidor.
    private Thread listenerThread;
    private volatile boolean listening = false;
 
    private final ObjectMapper mapper = new ObjectMapper();

    public void connect(String host, int port) throws IOException {

        socket = new Socket(host, port);

        reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );

        writer = new PrintWriter(
            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
            true
        );

        System.out.println("Conectado al servidor.");
    }

    public synchronized void send(Request request) throws IOException {

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

     public void startListening(IResponseListener listener) {

        if (listening) {
            return;
        }
        listening = true;

        listenerThread = new Thread(() -> {

            try {

                String json;

                while (listening && (json = reader.readLine()) != null) 
                {
                            Response response = mapper.readValue(json, Response.class);
                            listener.onResponse(response);
                }

            } catch (IOException e) {

                if (listening) {
                    listener.onError(e);
                }

            } finally {

                listening = false;

                System.out.println(
                    "Hilo de escucha finalizado."
                );
            }

        });

        listenerThread.setName("SocketClient-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }


    public void disconnect() throws IOException {

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}