package com.moviecatalogservice.resources;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;
import com.moviecatalogservice.services.MovieInfoService;
import com.moviecatalogservice.services.UserRatingService;
import com.moviecatalogservice.services.TrendingMoviesClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;
    private final MovieInfoService movieInfoService;
    private final UserRatingService userRatingService;
    private final TrendingMoviesClientService trendingClient;

    public MovieCatalogResource(RestTemplate restTemplate,
                                MovieInfoService movieInfoService,
                                UserRatingService userRatingService,
                                TrendingMoviesClientService trendingClient) {
        this.restTemplate = restTemplate;
        this.movieInfoService = movieInfoService;
        this.userRatingService = userRatingService;
        this.trendingClient = trendingClient;
    }

    // 1. ORIGINAL AGGREGATOR ENDPOINT: http://localhost:8081/catalog/{userId}
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId) {
        List<Rating> ratings = userRatingService.getUserRating(userId).getRatings();
        return ratings.stream().map(movieInfoService::getCatalogItem).collect(Collectors.toList());
    }

    // 2. TRENDING ENDPOINT: http://localhost:8081/catalog/trending
    @GetMapping("/trending")
    public List<CatalogItem> getTrendingCatalog() {
        com.example.trending.MovieList trendingList = trendingClient.getTrendingMovies();
        return trendingList.getMoviesList().stream().map(trendingMovie -> {
            Rating tempRating = new Rating(trendingMovie.getMovieId(), (int) trendingMovie.getRating());
            return movieInfoService.getCatalogItem(tempRating);
        }).collect(Collectors.toList());
    }

    // 3. NEW RATING ENDPOINT: http://localhost:8081/catalog/rating/{userId}
    @GetMapping("/rating/{userId}")
    public UserRating getDirectUserRating(@PathVariable String userId) {
        // Acts as a direct pass-through to the User Rating Service
        return userRatingService.getUserRating(userId);
    }

    // 4. NEW MOVIE INFO ENDPOINT: http://localhost:8081/catalog/movieinfo/{movieId}
    @GetMapping("/movieinfo/{movieId}")
    public Movie getDirectMovieInfo(@PathVariable String movieId) {
        // Acts as a direct pass-through to the Movie Info Service
        return movieInfoService.getMovie(movieId);
    }
}