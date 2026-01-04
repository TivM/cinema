package com.reactive.cinema.api;

import com.reactive.cinema.api.dto.NotificationDto;
import com.reactive.cinema.api.mapper.DtoMapper;
import com.reactive.cinema.application.notification.NotificationHub;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@Tag(name = "Notifications", description = "API для работы с уведомлениями и событиями")
public class NotificationController {
    private final NotificationHub hub;

    public NotificationController(NotificationHub hub) {
        this.hub = hub;
    }

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Стрим уведомлений", description = "Server-Sent Events стрим всех уведомлений системы")
    public Flux<ServerSentEvent<NotificationDto>> notifications() {
        return hub.stream()
                .map(DtoMapper::toDto)
                .map(dto -> ServerSentEvent.builder(dto)
                        .event(dto.type())
                        .build());
    }
}


