package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.models.Movie;
import com.example.movieinfoservice.models.MovieCache;
import com.example.movieinfoservice.repositories.MovieCacheRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/fake-movie-db")
public class FakeMovieResource {

    private final MovieCacheRepository cacheRepository;

    public FakeMovieResource(MovieCacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable String id) {

        long start = System.currentTimeMillis();

        // 1️⃣ CHECK CACHE FIRST
        Optional<MovieCache> cached = cacheRepository.findById(id);

        if (cached.isPresent()) {
            System.out.println("⚡ CACHE HIT");

            MovieCache cache = cached.get();

            long end = System.currentTimeMillis();
            System.out.println("Response time (CACHE): " + (end - start) + " ms");

            return new Movie(
                    cache.getMovieId(),
                    cache.getName(),
                    cache.getDescription()
            );
        }

        // 2️⃣ CACHE MISS → simulate slow API
        System.out.println("🌐 CACHE MISS → calling fake API");

        try {
            Thread.sleep(2000); // simulate slow external API
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Movie movie = new Movie(
                id,
                "Movie " + id,
                "Generated from fake API"
        );

        // 3️⃣ SAVE TO CACHE
        MovieCache movieCache = new MovieCache(
                movie.getMovieId(),
                movie.getName(),
                movie.getDescription()
        );

        cacheRepository.save(movieCache);

        long end = System.currentTimeMillis();
        System.out.println("Response time (API): " + (end - start) + " ms");

        return movie;
    }
}