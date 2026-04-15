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

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem",
        commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
        })
    public CatalogItem getCatalogItem(Rating rating) {
        // Points to the Fake DB endpoint in the Info Service
        String movieUrl = "http://movie-info-service/fake-movie-db/" + rating.getMovieId();
        Movie movie = restTemplate.getForObject(movieUrl, Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Fallback Movie", "External service slow or down", rating.getRating());
    }

    @HystrixCommand(fallbackMethod = "getFallbackMovie",
        commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
        })
    public Movie getMovie(String movieId) {
        // Points to the Fake DB endpoint in the Info Service
        String movieUrl = "http://movie-info-service/fake-movie-db/" + movieId;
        return restTemplate.getForObject(movieUrl, Movie.class);
    }

    public Movie getFallbackMovie(String movieId) {
        return new Movie(movieId, "Not Found", "Movie info service is currently down.");
    }
}