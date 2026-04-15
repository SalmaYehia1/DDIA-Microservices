package com.example.trending.services;

import com.example.trending.TrendingServiceGrpc;
import com.example.trending.Movie;
import com.example.trending.MovieList;
import com.example.trending.Empty;
import com.example.trending.repositories.TrendingRepository;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@GrpcService
public class TrendingServiceImpl extends TrendingServiceGrpc.TrendingServiceImplBase {

    @Autowired
    private TrendingRepository trendingRepository;

    @Override
    public void getTopMovies(Empty request, StreamObserver<MovieList> responseObserver) {
        
        // 1. Fetch the calculated top 10 movies from MySQL
        List<Object[]> results = trendingRepository.findTop10Movies();
        List<Movie> topMovies = new ArrayList<>();

        // 2. Loop through the database results and build the gRPC Movie objects
        for (Object[] row : results) {
            String movieId = (String) row[0];
            double avgRating = ((Number) row[1]).doubleValue();
            
            topMovies.add(Movie.newBuilder()
                    .setMovieId(movieId)
                    .setRating(avgRating)
                    .build());
        }

        // 3. Package them into the MovieList response
        MovieList response = MovieList.newBuilder()
                .addAllMovies(topMovies)
                .build();

        // 4. Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}