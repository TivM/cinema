package com.reactive.cinema.api.mapper;

import com.reactive.cinema.api.dto.MovieCardDto;
import com.reactive.cinema.api.dto.MovieDto;
import com.reactive.cinema.api.dto.NotificationDto;
import com.reactive.cinema.api.dto.PageResultDto;
import com.reactive.cinema.application.notification.NotificationEvent;
import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.PageResult;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static MovieDto toDto(Movie movie) {
        return new MovieDto(
                movie.id().value(),
                movie.title(),
                movie.type().name(),
                movie.description(),
                movie.createdAt()
        );
    }

    public static MovieCardDto toDto(MovieCard card) {
        return new MovieCardDto(
                toDto(card.movie()),
                card.avgRating(),
                card.userRating() == null ? null : card.userRating().value()
        );
    }

    public static <T> PageResultDto<T> toDto(PageResult<T> page) {
        return new PageResultDto<>(page.items(), page.page(), page.size(), page.total());
    }

    public static NotificationDto toDto(NotificationEvent event) {
        return new NotificationDto(
                event.type().name(),
                event.message(),
                event.at(),
                event.userId() == null ? null : event.userId().value(),
                event.movieId() == null ? null : event.movieId().value()
        );
    }
}


