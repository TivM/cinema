package com.reactive.cinema.api.dto;

import java.math.BigDecimal;

public record MovieCardDto(
        MovieDto movie,
        BigDecimal avgRating,
        Integer userRating
) {
}


