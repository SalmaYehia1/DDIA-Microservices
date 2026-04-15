package com.example.ratingsservice.models;
import javax.persistence.*;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "movie_id", nullable = false)
    private String movieId;
    
    @Column(name = "rating", nullable = false)
    private int rating;
    
    @Column(name = "user_id", nullable = false)
    private String userId;

    public Rating() {}

    public Long getId() { return id; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String m) { this.movieId = m; }
    public int getRating() { return rating; }
    public void setRating(int r) { this.rating = r; }
    public String getUserId() { return userId; }
    public void setUserId(String u) { this.userId = u; }
}