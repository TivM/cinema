package com.reactive.cinema.application.notification;

import reactor.core.publisher.Flux;

public interface NotificationHub {
    Flux<NotificationEvent> stream();

    void emit(NotificationEvent event);
}


