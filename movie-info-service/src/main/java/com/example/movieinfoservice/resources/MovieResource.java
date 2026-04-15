package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.models.Movie;
import com.example.movieinfoservice.models.MovieSummary;
import com.example.movieinfoservice.models.MovieCache;
import com.example.movieinfoservice.repositories.MovieCacheRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final MovieCacheRepository cacheRepository;

    public MovieResource(RestTemplate restTemplate,
                         MovieCacheRepository cacheRepository) {
        this.restTemplate = restTemplate;
        this.cacheRepository = cacheRepository;
    }

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {

        // 1️⃣ Check MongoDB first
        Optional<MovieCache> cachedMovie = cacheRepository.findById(movieId);

        if (cachedMovie.isPresent()) {
            System.out.println("Fetched from MongoDB cache");
            MovieCache cache = cachedMovie.get();
            return new Movie(
                    cache.getMovieId(),
                    cache.getName(),
                    cache.getDescription()
            );
        }

        // 2️⃣ Not in cache → Call TMDB
        System.out.println("Calling TMDB API");

        final String url = "https://api.themoviedb.org/3/movie/"
                + movieId + "?api_key=" + apiKey;

        MovieSummary movieSummary =
                restTemplate.getForObject(url, MovieSummary.class);

        Movie movie = new Movie(
                movieId,
                movieSummary.getTitle(),
                movieSummary.getOverview()
        );

        // 3️⃣ Save to MongoDB
        MovieCache movieCache = new MovieCache(
                movieId,
                movie.getName(),
                movie.getDescription()
        );

        cacheRepository.save(movieCache);

        return movie;
    }
}