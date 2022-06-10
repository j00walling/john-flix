package com.github.klefstad_teaching.cs122b.movies.model.data;

public class MovieDetail {
    private String overview;
    private Long revenue;
    private Boolean hidden;
    private Integer year;
    private String director;
    private Double rating;
    private Integer numVotes;
    private Long id;
    private String backdropPath;
    private String title;
    private String posterPath;
    private Long budget;

    public String getOverview() {
        return overview;
    }

    public MovieDetail setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public Long getRevenue() {
        return revenue;
    }

    public MovieDetail setRevenue(Long revenue) {
        this.revenue = revenue;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public MovieDetail setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public MovieDetail setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public MovieDetail setDirector(String director) {
        this.director = director;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public MovieDetail setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public MovieDetail setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
        return this;
    }

    public Long getId() {
        return id;
    }

    public MovieDetail setId(Long id) {
        this.id = id;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public MovieDetail setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MovieDetail setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public MovieDetail setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public Long getBudget() {
        return budget;
    }

    public MovieDetail setBudget(Long budget) {
        this.budget = budget;
        return this;
    }
}
