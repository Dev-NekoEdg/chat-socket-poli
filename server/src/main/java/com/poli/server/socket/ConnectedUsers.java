
package com.poli.server.socket;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectedUsers {

    private static final ConcurrentHashMap<String, ClientHandler>
            users = new ConcurrentHashMap<>();

    public static boolean add(String userId, ClientHandler client) {

        return users.putIfAbsent(userId, client) == null;
    }

    public static boolean remove(String userId, ClientHandler client) {

        return users.remove(userId, client);
    }

    public static ClientHandler get(String userId) {

        return users.get(userId);
    }

    public static Collection<ClientHandler> getAll() {

        return users.values();
    }

    public static boolean isConnected(String userId) {

        return users.containsKey(userId);
    }
}
