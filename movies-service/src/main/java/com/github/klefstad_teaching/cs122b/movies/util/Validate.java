package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;

import java.util.Optional;

public class Validate
{
    public static void validate(Optional<Integer> limit, Optional<Integer> page, Optional<String> orderBy, Optional<String> direction, Boolean movieSearch) {

        // Validate orderBy
        if (movieSearch) {
            if (orderBy.isPresent() && (!orderBy.get().equalsIgnoreCase("TITLE") && !orderBy.get().equalsIgnoreCase("RATING") && !orderBy.get().equalsIgnoreCase("YEAR"))) {
                throw new ResultError(MoviesResults.INVALID_ORDER_BY);
            }
        }
        else {
            if (orderBy.isPresent() && (!orderBy.get().equalsIgnoreCase("NAME") && !orderBy.get().equalsIgnoreCase("POPULARITY") && !orderBy.get().equalsIgnoreCase("BIRTHDAY"))) {
                throw new ResultError(MoviesResults.INVALID_ORDER_BY);
            }
        }



        // Validate limit
        if (limit.isPresent() && (limit.get() != 10 && limit.get() != 25 && limit.get() != 50 && limit.get() != 100)) {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }

        // Validate direction
        if (direction.isPresent() && (!direction.get().equalsIgnoreCase("ASC") && !direction.get().equalsIgnoreCase("DESC"))) {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }

        // Validate page
        if (page.isPresent() && page.get() <= 0) {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
    }
}
