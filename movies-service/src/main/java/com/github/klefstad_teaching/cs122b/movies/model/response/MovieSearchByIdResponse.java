package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.movies.model.data.Genre;
import com.github.klefstad_teaching.cs122b.movies.model.data.MovieDetail;
import com.github.klefstad_teaching.cs122b.movies.model.data.Person;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieSearchByIdResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MovieDetail movie;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Genre> genres;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Person> persons;

    public Result getResult() {
        return result;
    }

    public MovieSearchByIdResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public MovieDetail getMovie() {
        return movie;
    }

    public MovieSearchByIdResponse setMovie(MovieDetail movie) {
        this.movie = movie;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieSearchByIdResponse setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public MovieSearchByIdResponse setPersons(List<Person> persons) {
        this.persons = persons;
        return this;
    }
}
