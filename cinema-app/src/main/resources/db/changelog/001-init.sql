-- Enable extension
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Movies table
CREATE TABLE IF NOT EXISTS movies (
    id          uuid PRIMARY KEY,
    title       text        NOT NULL,
    type        text        NOT NULL,
    description text        NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT now()
);

-- User ratings
CREATE TABLE IF NOT EXISTS user_ratings (
    user_id    uuid        NOT NULL,
    movie_id   uuid        NOT NULL,
    rating     int         NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, movie_id),
    CONSTRAINT fk_user_ratings_movies FOREIGN KEY (movie_id) REFERENCES movies (id)
);

-- Seed data
INSERT INTO movies (id, title, type, description)
VALUES ('11111111-1111-1111-1111-111111111111', 'Reactive 101', 'MOVIE', 'Demo movie to validate reactive catalog.')
ON CONFLICT (id) DO NOTHING;

INSERT INTO movies (id, title, type, description)
VALUES ('22222222-2222-2222-2222-222222222222', 'Flux & Friends', 'SERIES', 'Demo series for rating/top-10 endpoints.')
ON CONFLICT (id) DO NOTHING;

INSERT INTO movies (id, title, type, description)
VALUES ('33333333-3333-3333-3333-333333333333', 'Backpressure Story', 'MOVIE', 'Demo movie for notifications stream.')
ON CONFLICT (id) DO NOTHING;


