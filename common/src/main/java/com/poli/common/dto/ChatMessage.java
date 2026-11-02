package com.poli.common.dto;

import java.time.LocalDateTime;

public record ChatMessage(
    String id,
    String roomId,
    String userId,
    String content,
    LocalDateTime createDate
) {}