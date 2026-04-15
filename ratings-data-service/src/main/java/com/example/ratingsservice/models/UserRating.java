package com.example.ratingsservice.models;
import java.util.List;

public class UserRating {
    private String userId;
    private List<Rating> ratings;

    public UserRating() {}
    public UserRating(String userId, List<Rating> ratings) {
        this.userId = userId;
        this.ratings = ratings;
    }

    public String getUserId() { return userId; }
    public void setUserId(String u) { this.userId = u; }
    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> r) { this.ratings = r; }
}