package com.moviecatalogservice.resources;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;
import com.moviecatalogservice.services.MovieInfoService;
import com.moviecatalogservice.services.UserRatingService;
import com.moviecatalogservice.services.TrendingMoviesClientService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;
    private final MovieInfoService movieInfoService;
    private final UserRatingService userRatingService;
    
    // 1. Declare the new trending client
    private final TrendingMoviesClientService trendingClient;

    // 2. Add the trending client to your constructor
    public MovieCatalogResource(RestTemplate restTemplate,
                                MovieInfoService movieInfoService,
                                UserRatingService userRatingService,
                                TrendingMoviesClientService trendingClient) {

        this.restTemplate = restTemplate;
        this.movieInfoService = movieInfoService;
        this.userRatingService = userRatingService;
        this.trendingClient = trendingClient;
    }

    /**
     * Makes a call to MovieInfoService to get movieId, name and description,
     * Makes a call to RatingsService to get ratings
     * Accumulates both data to create a MovieCatalog
     * @param userId
     * @return CatalogItem that contains name, description and rating
     */
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId) {
        List<Rating> ratings = userRatingService.getUserRating(userId).getRatings();
        return ratings.stream().map(movieInfoService::getCatalogItem).collect(Collectors.toList());
    }

    /**
     * NEW ENDPOINT:
     * Calls the TrendingService via gRPC to get the top 10 movies,
     * then fetches their details from MovieInfoService.
     * @return List of CatalogItems for the top trending movies
     */
    @GetMapping("/trending")
    public List<CatalogItem> getTrendingCatalog() {
        
        // Fetch top 10 movies from our gRPC Trending Service
        com.example.trending.MovieList trendingList = trendingClient.getTrendingMovies();
        
        // Loop through them, fetch movie info, and map to CatalogItems
        return trendingList.getMoviesList().stream().map(trendingMovie -> {
            
            // Get movie details (Name, Description) from Movie Info Service
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + trendingMovie.getMovieId(), Movie.class);
            
            // Combine the Movie Info with the gRPC Rating
            return new CatalogItem(movie.getName(), movie.getDescription(), (int) trendingMovie.getRating());
            
        }).collect(Collectors.toList());
    }
}