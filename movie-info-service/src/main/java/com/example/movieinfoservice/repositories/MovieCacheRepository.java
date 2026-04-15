package com.example.movieinfoservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.movieinfoservice.models.MovieCache;

public interface MovieCacheRepository extends MongoRepository<MovieCache, String> {
}