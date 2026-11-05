package com.poli.client.socket;

import com.poli.common.dto.Request;
import com.poli.common.dto.Response;
import com.poli.common.util.JsonUtil;

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

        if (writer == null) {
            throw new IOException("No hay conexión con el servidor");
        }

        writer.println(JsonUtil.toJson(request));

        if (writer.checkError()) {
            throw new IOException("No se pudo enviar la solicitud");
        }
    }

    /**
     * Lectura bloqueante de una sola respuesta. Solo debe usarse antes de
     * llamar a startListening (por ejemplo, durante el login).
     */
    public Response receive() throws IOException {

        String json = reader.readLine();

        if (json == null) {
            throw new IOException("El servidor cerró la conexión");
        }

        return JsonUtil.fromJson(json, Response.class);
    }

    public void startListening(IResponseListener listener) {

        if (listening) {
            return;
        }
        listening = true;

        listenerThread = new Thread(() -> {

            try {

                String json;

                while (listening && (json = reader.readLine()) != null) {
                    try {
                        listener.onResponse(JsonUtil.fromJson(json, Response.class));
                    } catch (UncheckedIOException e) {
                        System.err.println("Respuesta inválida del servidor: " + json);
                    }
                }

                // readLine() == null: el servidor cerró la conexión.
                if (listening) {
                    listener.onError(new IOException("El servidor cerró la conexión"));
                }

            } catch (IOException e) {

                if (listening) {
                    listener.onError(e);
                }

            } finally {

                listening = false;

                System.out.println("Hilo de escucha finalizado.");
            }
        });

        listenerThread.setName("SocketClient-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void disconnect() throws IOException {

        // Se marca antes de cerrar para que el hilo de escucha
        // no reporte el cierre como un error.
        listening = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
