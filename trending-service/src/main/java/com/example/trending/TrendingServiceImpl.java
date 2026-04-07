package com.example.trending;

import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;
import java.util.*;

import java.util.ArrayList;
import java.util.List;

// Import generated gRPC classes
import com.example.trending.TrendingServiceGrpc;
import com.example.trending.Movie;
import com.example.trending.MovieList;
import com.example.trending.Empty;


// This tells Spring Boot that this is a gRPC service
@GrpcService
public class TrendingServiceImpl extends TrendingServiceGrpc.TrendingServiceImplBase {

    @Override
    public void getTopMovies(Empty request, StreamObserver<MovieList> responseObserver) {

        // TEMP fake data — you can replace this later with real ratings fetch
        List<Movie> movies = new ArrayList<>();

        movies.add(Movie.newBuilder().setMovieId("1").setRating(5).build());
        movies.add(Movie.newBuilder().setMovieId("2").setRating(3).build());
        movies.add(Movie.newBuilder().setMovieId("3").setRating(4).build());

        // Sort descending by rating
        movies.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));

        // Take top 10 movies
        List<Movie> topMovies = movies.subList(0, Math.min(10, movies.size()));

        // Build the response
        MovieList response = MovieList.newBuilder()
                .addAllMovies(topMovies)
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}