package com.reactive.cinema.api.dto;

import java.util.List;

public record PageResultDto<T>(
        List<T> items,
        int page,
        int size,
        long total
) {
}


