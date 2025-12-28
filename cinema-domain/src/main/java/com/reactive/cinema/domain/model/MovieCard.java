package com.reactive.cinema.domain.model;

import java.math.BigDecimal;

public record MovieCard(
        Movie movie,
        BigDecimal avgRating,
        Rating userRating
) {
}


