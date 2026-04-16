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
    private final FakeMovieResource fakeMovieResource; // Added for fallback

    // Constructor Injection
    public MovieResource(RestTemplate restTemplate,
                         MovieCacheRepository cacheRepository,
                         FakeMovieResource fakeMovieResource) {
        this.restTemplate = restTemplate;
        this.cacheRepository = cacheRepository;
        this.fakeMovieResource = fakeMovieResource;
    }

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {

        // 1️⃣ Check MongoDB first
        Optional<MovieCache> cachedMovie = cacheRepository.findById(movieId);

        if (cachedMovie.isPresent()) {
            System.out.println("⚡ Fetching from MongoDB cache: " + movieId);
            MovieCache cache = cachedMovie.get();
            return new Movie(
                    cache.getMovieId(),
                    cache.getName(),
                    cache.getDescription()
            );
        }

        // 2️⃣ Try calling TMDB API
        try {
            System.out.println("🌐 Calling TMDB API for: " + movieId);
            final String url = "https://api.themoviedb.org/3/movie/"
                    + movieId + "?api_key=" + apiKey;

            MovieSummary movieSummary = restTemplate.getForObject(url, MovieSummary.class);

            if (movieSummary != null && movieSummary.getTitle() != null) {
                Movie movie = new Movie(
                        movieId,
                        movieSummary.getTitle(),
                        movieSummary.getOverview()
                );

                // Save to MongoDB for future use
                saveToCache(movie);
                return movie;
            } else {
                throw new Exception("Movie not found in TMDB");
            }

        } catch (Exception e) {
            // 3️⃣ FALLBACK: If TMDB fails or ID is invalid, call the Fake API logic
            System.out.println("❌ TMDB Error for " + movieId + ": " + e.getMessage());
            System.out.println("🔄 Falling back to FakeMovieResource...");
            
            // This calls your FakeMovieResource.getMovie() directly
            return fakeMovieResource.getMovie(movieId);
        }
    }

    private void saveToCache(Movie movie) {
        MovieCache movieCache = new MovieCache(
                movie.getMovieId(),
                movie.getName(),
                movie.getDescription()
        );
        cacheRepository.save(movieCache);
    }
}