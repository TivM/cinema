package com.reactive.cinema.application.notification;

import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.UserId;

import java.time.Instant;

public record NotificationEvent(
        NotificationType type,
        String message,
        Instant at,
        UserId userId,
        MovieId movieId
) {
    public NotificationEvent {
        if (type == null) throw new IllegalArgumentException("type must not be null");
        if (message == null) message = "";
        if (at == null) at = Instant.now();
    }
}


