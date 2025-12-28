package com.reactive.cinema.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<RatingEntity, RatingId> {
    Optional<RatingEntity> findByIdUserIdAndIdMovieId(UUID userId, UUID movieId);

    @Query("select coalesce(avg(r.rating), 0) from RatingEntity r where r.id.movieId = :movieId")
    BigDecimal averageByMovie(@Param("movieId") UUID movieId);

    @Query("select r.id.movieId from RatingEntity r where r.id.userId = :userId order by r.rating desc, r.updatedAt desc")
    List<UUID> topByUser(@Param("userId") UUID userId, org.springframework.data.domain.Pageable pageable);
}


