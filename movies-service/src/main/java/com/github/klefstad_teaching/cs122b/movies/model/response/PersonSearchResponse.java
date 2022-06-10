package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.data.PersonDetail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonSearchResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PersonDetail> persons;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonDetail person;

    public Result getResult() {
        return result;
    }

    public PersonSearchResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<PersonDetail> getPersons() {
        return persons;
    }

    public PersonSearchResponse setPersons(List<PersonDetail> persons) {
        this.persons = persons;
        return this;
    }

    public PersonDetail getPerson() {
        return person;
    }

    public PersonSearchResponse setPerson(PersonDetail person) {
        this.person = person;
        return this;
    }
}
