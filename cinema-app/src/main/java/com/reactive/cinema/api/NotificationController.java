package com.reactive.cinema.api;

import com.reactive.cinema.api.dto.NotificationDto;
import com.reactive.cinema.api.mapper.DtoMapper;
import com.reactive.cinema.application.notification.NotificationEvent;
import com.reactive.cinema.application.notification.NotificationHub;
import com.reactive.cinema.application.notification.NotificationType;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.UserId;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class NotificationController {
    private final NotificationHub hub;

    public NotificationController(NotificationHub hub) {
        this.hub = hub;
    }

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationDto>> notifications() {
        return hub.stream()
                .map(DtoMapper::toDto)
                .map(dto -> ServerSentEvent.builder(dto)
                        .event(dto.type())
                        .build());
    }

    @PostMapping("/streams/start")
    public void streamStarted(@RequestParam UUID movieId, @RequestParam(required = false) UUID userId) {
        hub.emit(new NotificationEvent(
                NotificationType.STREAM_STARTED,
                "Stream started",
                Instant.now(),
                userId == null ? null : UserId.of(userId),
                MovieId.of(movieId)
        ));
    }
}


