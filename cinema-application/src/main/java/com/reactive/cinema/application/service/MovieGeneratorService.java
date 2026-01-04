package com.reactive.cinema.application.service;

import com.reactive.cinema.application.notification.NotificationEvent;
import com.reactive.cinema.application.notification.NotificationHub;
import com.reactive.cinema.application.notification.NotificationType;
import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.MovieType;
import com.reactive.cinema.domain.port.MoviePort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class MovieGeneratorService {
    private final MoviePort moviePort;
    private final NotificationHub notificationHub;
    private final Random random = new Random();
    private int counter = 0;

    private static final List<String> MOVIE_TITLES = List.of(
            "The Reactive Chronicles",
            "Flux Adventures",
            "Mono Quest",
            "Backpressure Wars",
            "Stream of Dreams",
            "Reactor Rising",
            "The Async Awakening",
            "Non-Blocking Nights",
            "Event-Driven Empire",
            "Reactive Revolution"
    );

    private static final List<String> SERIES_TITLES = List.of(
            "Flux & Friends",
            "Mono Mysteries",
            "The Reactive Files",
            "Stream Chronicles",
            "Backpressure Tales"
    );

    private static final List<String> DESCRIPTIONS = List.of(
            "An epic journey through reactive programming",
            "A thrilling adventure in the world of non-blocking I/O",
            "Discover the power of reactive streams",
            "A story about handling backpressure",
            "Experience the magic of event-driven architecture"
    );

    public MovieGeneratorService(MoviePort moviePort, NotificationHub notificationHub) {
        this.moviePort = moviePort;
        this.notificationHub = notificationHub;
    }

    @Scheduled(initialDelay = 30000, fixedRate = 30000)
    public void generateMovie() {
        counter++;
        boolean isSeries = random.nextBoolean();
        List<String> titles = isSeries ? SERIES_TITLES : MOVIE_TITLES;
        String title = titles.get(random.nextInt(titles.size())) + " #" + counter;
        String description = DESCRIPTIONS.get(random.nextInt(DESCRIPTIONS.size()));
        MovieType type = isSeries ? MovieType.SERIES : MovieType.MOVIE;

        Movie movie = new Movie(
                MovieId.of(UUID.randomUUID()),
                title,
                type,
                description,
                Instant.now()
        );

        moviePort.create(movie)
                .doOnSuccess(createdMovie -> notificationHub.emit(new NotificationEvent(
                        NotificationType.NEW_MOVIE_ADDED,
                        "Новый фильм добавлен в каталог: " + createdMovie.title(),
                        Instant.now(),
                        null,
                        createdMovie.id()
                )))
                .subscribe();
    }
}

