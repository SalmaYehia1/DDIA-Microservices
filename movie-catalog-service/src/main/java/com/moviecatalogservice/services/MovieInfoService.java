package com.moviecatalogservice.services;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfoService {

    private final RestTemplate restTemplate;

    public MovieInfoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // EXISTING METHOD (Used for Aggregation)
    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem",
        commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
        })
    public CatalogItem getCatalogItem(Rating rating) {
        String movieUrl = "http://movie-info-service/movies/" + rating.getMovieId();
        Movie movie = restTemplate.getForObject(movieUrl, Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie name not found", "Description unavailable", rating.getRating());
    }

    // NEW METHOD: Exposes raw Movie Info
    @HystrixCommand(fallbackMethod = "getFallbackMovie")
    public Movie getMovie(String movieId) {
        String movieUrl = "http://movie-info-service/movies/" + movieId;
        return restTemplate.getForObject(movieUrl, Movie.class);
    }

    public Movie getFallbackMovie(String movieId) {
        return new Movie(movieId, "Not Found", "Movie info service is currently down.");
    }
}