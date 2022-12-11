package com.github.j00walling.idm.repo;


import com.github.j00walling.idm.repo.entity.RefreshToken;
import com.github.j00walling.idm.repo.entity.User;
import com.github.j00walling.idm.repo.entity.type.TokenStatus;
import com.github.j00walling.idm.repo.entity.type.UserStatus;
import com.github.j00walling.idm.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Component
public class IDMRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public ResponseEntity<Validate> insertUser(User user) {
        Validate response = new Validate();

        try {
            this.template.update(
            "INSERT INTO idm.user (email, user_status_id, salt, hashed_password)" +
                "VALUES (:email, :userStatusID, :salt, :hashedPassword);",

                new MapSqlParameterSource()
                        .addValue("email", user.getEmail(), Types.VARCHAR)
                        .addValue("userStatusID", user.getUserStatus().id(), Types.INTEGER)
                        .addValue("salt", user.getSalt(), Types.CHAR)
                        .addValue("hashedPassword", user.getHashedPassword(), Types.CHAR)

            );
        }
        catch (Exception e) {
//            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }
//        response.setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public User selectUser(String email) {
        List<User> userList =
            this.template.query (
            "SELECT id, email, user_status_id, salt, hashed_password " +
                "FROM idm.user " +
                "WHERE email = :email;",

                new MapSqlParameterSource()
                        .addValue("email", email, Types.VARCHAR),

                (rs, rowNum) -> new User()
                        .setId(rs.getInt("id"))
                        .setEmail(rs.getString("email"))
                        .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                        .setSalt(rs.getString("salt"))
                        .setHashedPassword(rs.getString("hashed_password"))
            );

        if (userList.size() != 1) {
//            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }

        return userList.get(0);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken) {
        List<User> userIDList =
                this.template.query(
                "SELECT user_id " +
                    "FROM idm.refresh_token " +
                    "WHERE token = :token;",

                    new MapSqlParameterSource()
                            .addValue("token", refreshToken.getToken(), Types.CHAR),

                    (rs, rowNum) -> new User()
                            .setId(rs.getInt("user_id"))
                );

        if (userIDList.size() != 1) {
//            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }

        List<User> userList =
            this.template.query (
                "SELECT id, email, user_status_id, salt, hashed_password " +
                    "FROM idm.user " +
                    "WHERE id = :userID;",

                    new MapSqlParameterSource()
                            .addValue("userID", userIDList.get(0).getId(), Types.INTEGER),

                    (rs, rowNum) -> new User()
                            .setId(rs.getInt("id"))
                            .setEmail(rs.getString("email"))
                            .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                            .setSalt(rs.getString("salt"))
                            .setHashedPassword(rs.getString("hashed_password"))
            );

        if (userList.size() != 1) {
//            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }

        return userList.get(0);
    }

    public void insertRefreshToken(RefreshToken refreshToken) {
        this.template.update(
        "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time) " +
            "VALUES (:token, :userID, :tokenStatusID, :expireTime, :maxLifeTime);",

            new MapSqlParameterSource()
                    .addValue("token", refreshToken.getToken(), Types.CHAR)
                    .addValue("userID", refreshToken.getUserId(), Types.INTEGER)
                    .addValue("tokenStatusID", refreshToken.getTokenStatus().id(), Types.INTEGER)
                    .addValue("expireTime", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                    .addValue("maxLifeTime", Timestamp.from(refreshToken.getMaxLifeTime()), Types.TIMESTAMP)
        );
    }

    public RefreshToken getRefreshToken(String token) {
        List<RefreshToken> refreshTokenList =
            this.template.query (
            "SELECT id, token, user_id, token_status_id, expire_time, max_life_time " +
                "FROM idm.refresh_token " +
                "WHERE token = :token;",

                new MapSqlParameterSource()
                        .addValue("token", token, Types.CHAR),

                (rs, rowNum) -> new RefreshToken()
                        .setId(rs.getInt("id"))
                        .setToken(rs.getString("token"))
                        .setUserId(rs.getInt("user_id"))
                        .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                        .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                        .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
            );

        if (refreshTokenList.size() != 1) {
//            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }

        return refreshTokenList.get(0);
    }

    public void setRefreshTokenStatus(RefreshToken refreshToken, int status) {
        this.template.update(
        "UPDATE idm.refresh_token " +
            "SET token_status_id = :tokenStatusID " +
            "WHERE token = :token;",

            new MapSqlParameterSource()
                    .addValue("tokenStatusID", status, Types.INTEGER)
                    .addValue("token", refreshToken.getToken(), Types.CHAR)
        );
    }

    public void updateRefreshTokenExpireTime(RefreshToken token, Instant newExpireTime) {
        this.template.update(
        "UPDATE idm.refresh_token " +
            "SET expire_time = :expireTime " +
            "WHERE token = :token;",

            new MapSqlParameterSource()
                .addValue("expireTime", Timestamp.from(newExpireTime), Types.TIMESTAMP)
                .addValue("token", token.getToken(), Types.CHAR)
        );
    }
}
