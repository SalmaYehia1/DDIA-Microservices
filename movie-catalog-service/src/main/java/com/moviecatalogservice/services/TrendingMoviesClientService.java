package com.moviecatalogservice.services;

import com.example.trending.TrendingServiceGrpc;
import com.example.trending.Empty;
import com.example.trending.MovieList;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class TrendingMoviesClientService {

    @GrpcClient("trending-service")
    private TrendingServiceGrpc.TrendingServiceBlockingStub trendingStub;

    public MovieList getTrendingMovies() {
        Empty request = Empty.newBuilder().build();
        return trendingStub.getTopMovies(request);
    }
}
