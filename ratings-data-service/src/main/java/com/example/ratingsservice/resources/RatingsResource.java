package com.example.ratingsservice.resources;

import com.example.ratingsservice.models.*;
import com.example.ratingsservice.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController 
//http requests will be handled by this class
@RequestMapping("/ratings")
public class RatingsResource {

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/{userId}")
    public UserRating getUserRatings(@PathVariable("userId") String userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return new UserRating(userId, ratings);  //DTO
    }

    @PostMapping
    public Rating addRating(@RequestBody Rating rating) {
        return ratingRepository.save(rating);
    }
}