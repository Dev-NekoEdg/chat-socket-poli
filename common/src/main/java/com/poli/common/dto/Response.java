package com.poli.common.dto;

import com.poli.common.enums.MessageType;

public record Response(
    boolean success,
    MessageType type,
    String message,
    String data
) {}

/*
CONNECT,
    CONNECTED,

    CHAT,
    CHAT_HISTORY,

    CREATE_ROOM,
    JOIN_ROOM,
    LEAVE_ROOM,

    SELECT,
    INSERT,
    UPDATE,
    DELETE,

    DISCONNECT,

    ERROR
*/