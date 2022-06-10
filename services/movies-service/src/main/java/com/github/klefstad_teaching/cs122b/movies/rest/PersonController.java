package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.data.Movie;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.github.klefstad_teaching.cs122b.movies.util.VerifyAdmin;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;

@RestController
public class PersonController
{
    private final MovieRepo repo;

    @Autowired
    public PersonController(MovieRepo repo)
    {
        this.repo = repo;
    }

    @GetMapping({"/person/search"})
    public ResponseEntity<PersonSearchResponse> personSearch (
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam() Optional<String> name,
            @RequestParam() Optional<String> birthday,
            @RequestParam() Optional<String> movieTitle,
            @RequestParam() Optional<Integer> limit,
            @RequestParam() Optional<Integer> page,
            @RequestParam() Optional<String> orderBy,
            @RequestParam() Optional<String> direction
    ) {
        // Validate
        Validate.validate(limit, page, orderBy, direction, false);

        PersonSearchResponse response =
                repo.personSearch(
                        name.isPresent() ? name.get() : "",
                        birthday.isPresent() ? birthday.get() : "",
                        movieTitle.isPresent() ? movieTitle.get() : "",
                        limit.isPresent() ? limit.get() : null,
                        page.isPresent() ? page.get() : 1,
                        orderBy.isPresent() ? orderBy.get() : null,
                        direction.isPresent() ? direction.get() : null);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping({"/person/{personId}"})
    public ResponseEntity<PersonSearchResponse> personSearchById (
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long personId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(repo.personSearchById(personId));
    }
}
