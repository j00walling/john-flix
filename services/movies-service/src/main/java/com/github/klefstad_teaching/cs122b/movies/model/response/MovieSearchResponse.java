package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.movies.model.data.Genre;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieDetail;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieSearchResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Movie> movies;

    public Result getResult() {
        return result;
    }

    public MovieSearchResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public MovieSearchResponse setMovies(List<Movie> movies) {
        this.movies = movies;
        return this;
    }
}
