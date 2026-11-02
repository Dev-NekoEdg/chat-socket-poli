package com.poli.common.dto;

import com.poli.common.enums.MessageType;

public record Response(
    boolean success,
    MessageType type,
    String message,
    String data
) {}