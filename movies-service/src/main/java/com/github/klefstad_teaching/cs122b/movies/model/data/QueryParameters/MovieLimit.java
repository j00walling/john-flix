package com.github.klefstad_teaching.cs122b.movies.model.data.QueryParameters;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;

public enum MovieLimit {
    TEN(" LIMIT 10 "),
    TWENTY_FIVE(" LIMIT 25 "),
    FIFTY(" LIMIT 50 "),
    ONE_HUNDRED(" LIMIT 100 ");

    private final String sql;

    MovieLimit(String sql) { this.sql = sql; }

    public String toSql() { return sql; }

    public static MovieLimit fromInteger(Integer limit) {
        if (limit == null)
            return TEN;

        switch (limit) {
            case 10:
                return TEN;
            case 25:
                return TWENTY_FIVE;
            case 50:
                return FIFTY;
            case 100:
                return ONE_HUNDRED;
            default:
                throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
    }
}
