package com.reactive.cinema.config;

import com.reactive.cinema.application.notification.InMemoryNotificationHub;
import com.reactive.cinema.application.notification.NotificationHub;
import com.reactive.cinema.application.service.CatalogService;
import com.reactive.cinema.application.service.DefaultCatalogService;
import com.reactive.cinema.application.service.DefaultRatingService;
import com.reactive.cinema.application.service.RatingService;
import com.reactive.cinema.domain.port.MoviePort;
import com.reactive.cinema.domain.port.RatingPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public NotificationHub notificationHub() {
        return new InMemoryNotificationHub();
    }

    @Bean
    public CatalogService catalogService(MoviePort moviePort, RatingPort ratingPort) {
        return new DefaultCatalogService(moviePort, ratingPort);
    }

    @Bean
    public RatingService ratingService(MoviePort moviePort, RatingPort ratingPort, NotificationHub hub) {
        return new DefaultRatingService(moviePort, ratingPort, hub);
    }
}


