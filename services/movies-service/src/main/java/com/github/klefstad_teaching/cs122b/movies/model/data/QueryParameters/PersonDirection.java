package com.github.klefstad_teaching.cs122b.movies.model.data.QueryParameters;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;

import java.util.Locale;

public enum PersonDirection {
    ASC(" ASC, p.id "),
    DESC(" DESC, p.id ");

    private final String sql;

    PersonDirection(String sql) { this.sql = sql; }

    public String toSql() { return this.sql; }

    public static PersonDirection fromString (String direction) {
        if (direction == null)
            return ASC;

        switch (direction.toUpperCase(Locale.ROOT)) {
            case "ASC":
                return ASC;
            case "DESC":
                return DESC;
            default:
                throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }
}
