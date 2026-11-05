package com.poli.common.enums;

public enum MessageType {

    CONNECT,
    CONNECTED,

    CHAT,
    CHAT_HISTORY,

    CREATE_ROOM,
    JOIN_ROOM,
    LEAVE_ROOM,
    // Salas relacionadas con el usuario (Response.data = List<ChatRoom> en JSON)
    LIST_ROOMS,
    // Usuarios disponibles para agregar a una sala (Response.data = List<String> en JSON)
    LIST_USERS,

    SELECT,
    INSERT,
    UPDATE,
    DELETE,

    DISCONNECT,

    ERROR
}
