package com.example.movieinfoservice.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "movies")
public class MovieCache {

    @Id
    private String movieId;

    private String name;
    private String description;

    @Indexed(expireAfterSeconds = 86400)
    private Instant cachedAt;

    public MovieCache() {}

    public MovieCache(String movieId, String name, String description) {
        this.movieId = movieId;
        this.name = name;
        this.description = description;
        this.cachedAt = Instant.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCachedAt(){return cachedAt;}

    public void setCachedAt(Instant cachedAt){this.cachedAt = cachedAt;}

}