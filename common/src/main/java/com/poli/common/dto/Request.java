package com.poli.common.dto;

import com.poli.common.enums.MessageType;

    public record Request(
    MessageType type,
    String userId,
    String roomId,
    String content
) {}
