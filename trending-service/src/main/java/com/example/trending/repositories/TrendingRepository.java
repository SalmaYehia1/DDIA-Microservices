package com.example.trending.repositories;

import com.example.trending.entities.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrendingRepository extends JpaRepository<RatingEntity, Long> {
    
    // Native query to group by movie, calculate average, sort descending, and limit to top 10
    @Query(value = "SELECT movie_id, AVG(rating) as avg_rating FROM ratings GROUP BY movie_id ORDER BY avg_rating DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10Movies();
}