package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchByIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
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
import java.util.List;
import java.util.Optional;


@RestController
public class MovieController
{
    private final MovieRepo repo;

    @Autowired
    public MovieController(MovieRepo repo)
    {
        this.repo = repo;
    }

    @GetMapping({"/movie/search"})
    public ResponseEntity<MovieSearchResponse> movieSearch (
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam() Optional<String> title,
            @RequestParam() Optional<Integer> year,
            @RequestParam() Optional<String> director,
            @RequestParam() Optional<String> genre,
            @RequestParam() Optional<Integer> limit,
            @RequestParam() Optional<Integer> page,
            @RequestParam() Optional<String> orderBy,
            @RequestParam() Optional<String> direction
            ) throws ParseException {

        // Validate
        Validate.validate(limit, page, orderBy, direction, true);

        // Check if admin or employee
        Boolean admin = VerifyAdmin.verify(user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES));

        MovieSearchResponse response =
                repo.movieSearch(
                        title.isPresent() ? title.get() : "",
                        year.isPresent() ? year.get() : null,
                        director.isPresent() ? director.get() : "",
                        genre.isPresent() ? genre.get() : "",
                        admin,
                        orderBy.isPresent() ? orderBy.get() : null,
                        limit.isPresent() ? limit.get() : null,
                        direction.isPresent() ? direction.get() : null,
                        page.isPresent() ? page.get() : 1);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping({"/movie/search/person/{personId}"})
    public ResponseEntity<MovieSearchResponse> movieSearchByPersonId (
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long personId,
            @RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> orderBy,
            @RequestParam Optional<String> direction) throws ParseException {

        // Validate
        Validate.validate(limit, page, orderBy, direction, true);

        // Check if admin/employee
        Boolean admin = VerifyAdmin.verify(user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES));

        MovieSearchResponse response = this.repo.movieSearchByPersonId(admin, personId,
                orderBy.isPresent() ? orderBy.get() : null,
                limit.isPresent() ? limit.get() : null,
                direction.isPresent() ? direction.get() : null,
                page.isPresent() ? page.get() : 1);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping({"/movie/{movieId}"})
    public ResponseEntity<MovieSearchByIdResponse> movieSearchByMovieId (
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId) throws ParseException {

        // Check if admin/employee
        Boolean admin = VerifyAdmin.verify(user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES));

        MovieSearchByIdResponse response = this.repo.movieSearchByMovieId(admin, movieId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
