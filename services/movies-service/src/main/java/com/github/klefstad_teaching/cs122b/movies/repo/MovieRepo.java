package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.model.data.*;
import com.github.klefstad_teaching.cs122b.movies.model.data.QueryParameters.*;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchByIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.model.response.PersonSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieRepo {
    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate template;

    private static String MOVIE_SEARCH =
            "SELECT JSON_ARRAYAGG(JSON_OBJECT( " +
                    "    'id', m.id, " +
                    "    'title', m.title, " +
                    "    'year', m.year, " +
                    "    'director', m.name, " +
                    "    'rating', m.rating, " +
                    "    'backdropPath', m.backdrop_path, " +
                    "    'posterPath', m.poster_path, " +
                    "    'hidden', m.hidden " +
                    "    )) AS movieSearch " +
                    "FROM (SELECT DISTINCT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                    "      FROM movies.movie m " +
                    "          INNER JOIN movies.person p ON p.id = m.director_id ";

    private static String PERSON_SEARCH =
            "SELECT JSON_ARRAYAGG(JSON_OBJECT( " +
            "   'id', p.id, " +
            "   'name', p.name, " +
            "   'birthday', p.birthday, " +
            "   'biography', p.biography, " +
            "   'birthplace', p.birthplace, " +
            "   'popularity', p.popularity, " +
            "   'profilePath', p.profile_path)) AS personSearch " +
            "FROM " +
            "   (SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path " +
            "    FROM movies.person p ";

    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template) {
        this.objectMapper = objectMapper;
        this.template = template;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public MovieSearchResponse movieSearch(String title, Integer year, String director, String genre, Boolean admin, String orderBy, Integer limit, String direction, Integer page) {
        StringBuilder sql = new StringBuilder(MOVIE_SEARCH);
        MapSqlParameterSource source = new MapSqlParameterSource();

        if (!title.isEmpty() || year != null || !director.isEmpty() || !genre.isEmpty()) {
            if (!genre.isEmpty()) {
                sql.append(" INNER JOIN movies.movie_genre mg ON mg.movie_id = m.id" +
                           " INNER JOIN movies.genre g ON g.id = mg.genre_id ");
            }

            sql.append(" WHERE ");

            if (!title.isEmpty()) {
                String wildcard_title = '%' + title + '%';

                sql.append(" m.title LIKE :title AND ");
                source.addValue("title", wildcard_title, Types.VARCHAR);
            }
            if (year != null) {
                sql.append(" m.year = :year AND ");
                source.addValue("year", year, Types.INTEGER);
            }
            if (!director.isEmpty()) {
                String wildcard_director = '%' + director + '%';

                sql.append(" p.name LIKE :director AND ");
                source.addValue("director", wildcard_director, Types.VARCHAR);
            }
            if (!genre.isEmpty()) {
                sql.append(" g.name LIKE :genre AND ");
                String wildcard_genre = '%' + genre + '%';
                source.addValue("genre", wildcard_genre, Types.VARCHAR);
            }
            if (!admin) {
                sql.append(" m.hidden=false ");
            } else {
                sql = new StringBuilder(sql.substring(0, sql.length() - 4));
            }
        }

        // Order by
        MovieOrderBy o = MovieOrderBy.fromString(orderBy);
        sql.append(o.toSql());

        // Direction
        MovieDirection d = MovieDirection.fromString(direction);
        sql.append(d.toSql());

        // Limit
        MovieLimit l = MovieLimit.fromInteger(limit);
        sql.append(l.toSql());

        // Pagination
        String offset;

        if (limit == null) {
            offset = Integer.toString((page - 1) * 10);
        } else {
            offset = Integer.toString((page - 1) * limit);
        }

        sql.append(" OFFSET " + offset + " ) AS m");

        return this.template.queryForObject(
                sql.toString(),
                source,
                this::movieMapping
        );
    }
    public MovieSearchResponse movieSearchByPersonId(Boolean admin, Long personId, String orderBy, Integer limit, String direction, Integer page) {
        String MOVIE_SEARCH_BY_PERSON_ID = "SELECT JSON_ARRAYAGG(JSON_OBJECT( " +
                "    'id', m.id, " +
                "    'title', m.title, " +
                "    'year', m.year, " +
                "    'director', m.name, " +
                "    'rating', m.rating, " +
                "    'backdropPath', m.backdrop_path, " +
                "    'posterPath', m.poster_path, " +
                "    'hidden', m.hidden " +
                "    )) AS movieSearch " +
                "FROM (SELECT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                "      FROM movies.movie m " +
                "          INNER JOIN movies.movie_person mp ON mp.movie_id = m.id " +
                "          INNER JOIN movies.person p ON p.id = m.director_id " +
                "          INNER JOIN movies.person pp ON pp.id = mp.person_id AND pp.id = :personId";
        StringBuilder sql = new StringBuilder(MOVIE_SEARCH_BY_PERSON_ID);

        if (!admin) {
            sql.append(" WHERE m.hidden=false ");
        }

        // Order by
        MovieOrderBy o = MovieOrderBy.fromString(orderBy);
        sql.append(o.toSql());

        // Direction
        MovieDirection d = MovieDirection.fromString(direction);
        sql.append(d.toSql());

        // Limit
        MovieLimit l = MovieLimit.fromInteger(limit);
        sql.append(l.toSql());

        // Pagination
        String offset;

        if (limit == null) {
            offset = Integer.toString((page - 1) * 10);
        } else {
            offset = Integer.toString((page - 1) * limit);
        }

        sql.append(" OFFSET ").append(offset).append(" ) AS m");

        return this.template.queryForObject(
                sql.toString(),
                new MapSqlParameterSource().addValue("personId", personId.intValue(), Types.INTEGER),
                this::movieMappingFromPersonId
        );
    }
    public MovieSearchByIdResponse movieSearchByMovieId(Boolean admin, Long movieId) {
        String MOVIE_SEARCH_BY_MOVIE_ID = "SELECT m.id, m.title, m.year, p.name, m.rating, m.num_votes, m.budget, m.revenue, m.overview, m.backdrop_path, m.poster_path, m.hidden," +
                "(SELECT JSON_ARRAYAGG(JSON_OBJECT('id', g.id, 'name', g.name)) " +
                " FROM (SELECT DISTINCT g.id, g.name " +
                "   FROM movies.genre g " +
                "       INNER JOIN movies.movie_genre mg ON g.id = mg.genre_id " +
                "   WHERE mg.movie_id = :movieId " +
                "   ORDER BY g.name) AS g) AS genres, " +
                " (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', p.id, 'name', p.name)) " +
                " FROM (SELECT DISTINCT p.id, p.name, p.popularity " +
                "FROM movies.person p " +
                "INNER JOIN movies.movie_person mp " +
                "   ON p.id = mp.person_id " +
                "       AND mp.movie_id = :movieId " +
                "ORDER BY p.popularity DESC, p.id ASC) AS p) AS persons " +
                "FROM movies.movie m " +
                "INNER JOIN movies.person p ON p.id = m.director_id " +
                "WHERE m.id = :movieId ";
        StringBuilder sql = new StringBuilder(MOVIE_SEARCH_BY_MOVIE_ID);

        if (!admin) {
            sql.append(" AND m.hidden=false ");
        }
        try {
            return this.template.queryForObject(
                    sql.toString(),
                    new MapSqlParameterSource().addValue("movieId", movieId.intValue(), Types.INTEGER),
                    this::movieMappingFromMovieId
            );
        }
        catch (EmptyResultDataAccessException e) {
            throw new ResultError(MoviesResults.NO_MOVIE_WITH_ID_FOUND);
        }

    }
    private MovieSearchResponse movieMapping(ResultSet rs, int rowNumber) throws SQLException {
        List<Movie> movies;
        MovieSearchResponse response = new MovieSearchResponse();

        try {
            String jsonArrayString = rs.getString("movieSearch");

            if (jsonArrayString == null) {
                return response
                        .setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH);
            }

            Movie[] movieArray =
                    objectMapper.readValue(jsonArrayString, Movie[].class);

            movies = Arrays.stream(movieArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map 'movies' to Movie[]");
        }

        return response
                .setMovies(movies)
                .setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH);
    }
    private MovieSearchResponse movieMappingFromPersonId(ResultSet rs, int rowNumber) throws SQLException {
        List<Movie> movies;
        MovieSearchResponse response = new MovieSearchResponse();

        try {
            String jsonArrayString = rs.getString("movieSearch");

            if (jsonArrayString == null) {
                return response
                        .setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND);
            }

            Movie[] movieArray =
                    objectMapper.readValue(jsonArrayString, Movie[].class);

            movies = Arrays.stream(movieArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map 'movies' to Movie[]");
        }

        return response
                .setMovies(movies)
                .setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND);
    }
    private MovieSearchByIdResponse movieMappingFromMovieId(ResultSet rs, int rowNumber) throws SQLException {
        List<Genre> genres = null;
        List<Person> persons = null;
        MovieSearchByIdResponse response = new MovieSearchByIdResponse();

        try {
            String genresJsonArrayString = rs.getString("genres");
            String personsJsonArrayString = rs.getString("persons");

            if (genresJsonArrayString != null) {
                Genre[] genreArray =
                        objectMapper.readValue(genresJsonArrayString, Genre[].class);

                genres = Arrays.stream(genreArray).collect(Collectors.toList());
            }

            if (personsJsonArrayString != null) {
                Person[] personArray =
                        objectMapper.readValue(personsJsonArrayString, Person[].class);

                persons = Arrays.stream(personArray).collect(Collectors.toList());
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map");
        }

        MovieDetail movie = new MovieDetail()
                .setHidden(rs.getBoolean("hidden"))
                .setYear(rs.getInt("year"))
                .setDirector(rs.getString("name"))
                .setRating(rs.getDouble("rating"))
                .setId(rs.getLong("id"))
                .setBackdropPath(rs.getString("backdrop_path"))
                .setTitle(rs.getString("title"))
                .setNumVotes(rs.getInt("num_votes"))
                .setBudget(rs.getLong("budget"))
                .setRevenue(rs.getLong("revenue"))
                .setOverview(rs.getString("overview"))
                .setPosterPath(rs.getString("poster_path"));

        return response
                .setMovie(movie)
                .setGenres(genres)
                .setPersons(persons)
                .setResult(MoviesResults.MOVIE_WITH_ID_FOUND);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public PersonSearchResponse personSearch(String name, String birthday, String movieTitle, Integer limit, Integer page, String orderBy, String direction) {
        StringBuilder sql = new StringBuilder(PERSON_SEARCH);
        MapSqlParameterSource source = new MapSqlParameterSource();

        if (!name.isEmpty() || !birthday.isEmpty() || !movieTitle.isEmpty()) {
            if (!movieTitle.isEmpty()) {
                sql.append(" INNER JOIN movies.movie_person mp ON mp.person_id = p.id INNER JOIN movies.movie m ON mp.movie_id = m.id ");
            }

            sql.append(" WHERE ");

            if (!name.isEmpty()) {
                String wildcard_name = '%' + name + '%';

                sql.append(" p.name LIKE :name AND ");
                source.addValue("name", wildcard_name, Types.VARCHAR);
            }
            if (!birthday.isEmpty()) {
                sql.append(" p.birthday = :birthday AND ");
                source.addValue("birthday", LocalDate.parse(birthday), Types.DATE);
            }
            if (!movieTitle.isEmpty()) {
                String wildcard_movie = '%' + movieTitle + '%';
                sql.append(" m.title LIKE :title AND ");
                source.addValue("title", wildcard_movie, Types.VARCHAR);
            }

            sql = new StringBuilder(sql.substring(0, sql.length() - 4));
        }

        // Order by
        PersonOrderBy o = PersonOrderBy.fromString(orderBy);
        sql.append(o.toSql());

        // Direction
        PersonDirection d = PersonDirection.fromString(direction);
        sql.append(d.toSql());

        // Limit
        MovieLimit l = MovieLimit.fromInteger(limit);
        sql.append(l.toSql());

        // Pagination
        String offset;

        if (limit == null) {
            offset = Integer.toString((page - 1) * 10);
        } else {
            offset = Integer.toString((page - 1) * limit);
        }

        sql.append(" OFFSET ").append(offset).append(" ) AS p");

        return this.template.queryForObject(
                sql.toString(),
                source,
                this::personMapping
        );
    }
    private PersonSearchResponse personMapping(ResultSet rs, int rowNumber) {
        List<PersonDetail> persons = null;
        PersonSearchResponse response = new PersonSearchResponse();

        try {
            String jsonArrayString = rs.getString("personSearch");
            System.out.println(jsonArrayString);
            if (jsonArrayString == null) {
                return response
                        .setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH);
            }

            PersonDetail[] personArray =
                    objectMapper.readValue(jsonArrayString, PersonDetail[].class);

            persons = Arrays.stream(personArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map 'movies' to Movie[]");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response
                .setPersons(persons)
                .setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH);
    }
    public PersonSearchResponse personSearchById(Long personId) {
        String PERSON_SEARCH_BY_ID =
                "SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path " +
                "FROM movies.person p " +
                "WHERE p.id = :id " +
                "ORDER BY p.id;";

        List<PersonDetail> personDetailList = this.template.query(
                PERSON_SEARCH_BY_ID,
                new MapSqlParameterSource().addValue("id", personId.intValue(), Types.INTEGER),
                (rs, rowNum) ->
                    new PersonDetail()
                            .setId(rs.getLong("id"))
                            .setName(rs.getString("name"))
                            .setBirthday(rs.getString("birthday"))
                            .setBiography(rs.getString("biography"))
                            .setBirthplace(rs.getString("birthplace"))
                            .setPopularity(rs.getFloat("popularity"))
                            .setProfilePath(rs.getString("profile_path"))
        );

        if (personDetailList.size() != 1) {
            throw new ResultError(MoviesResults.NO_PERSON_WITH_ID_FOUND);
        }

        return new PersonSearchResponse()
                .setPerson(personDetailList.get(0))
                .setResult(MoviesResults.PERSON_WITH_ID_FOUND);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
