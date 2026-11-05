package com.poli.common.dto;

import java.util.List;

/**
 * Representa una sala de chat (conversación).
 *
 * @param id           identificador de la sala (null al crearla, lo asigna el servidor)
 * @param name         nombre visible de la sala
 * @param privateRoom  true si la sala es privada, false si es pública
 * @param participants nombres de usuario de los participantes
 */
public record ChatRoom(
    String id,
    String name,
    boolean privateRoom,
    List<String> participants
) {

    public ChatRoom {
        participants = participants == null ? List.of() : List.copyOf(participants);
    }

    public int participantCount() {
        return participants.size();
    }
}
