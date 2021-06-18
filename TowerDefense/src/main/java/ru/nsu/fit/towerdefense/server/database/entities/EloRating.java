package ru.nsu.fit.towerdefense.server.database.entities;

public class EloRating {
    private Long ratingId;
    private String user;
    private int rating;

    public EloRating()
    {
    }

    public int getRating()
    {
        return rating;
    }

    public Long getRatingId()
    {
        return ratingId;
    }

    public String getUser()
    {
        return user;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    public void setRatingId(Long ratingId)
    {
        this.ratingId = ratingId;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

}
