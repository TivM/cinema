package com.reactive.cinema.application.notification;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class InMemoryNotificationHub implements NotificationHub {
    private final Sinks.Many<NotificationEvent> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Flux<NotificationEvent> stream() {
        return sink.asFlux();
    }

    @Override
    public void emit(NotificationEvent event) {
        if (event == null) return;
        sink.tryEmitNext(event);
    }
}


