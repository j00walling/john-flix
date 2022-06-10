package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.model.request.AuthenticateRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.CredentialsRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.RefreshTokenRequest;
import com.github.klefstad_teaching.cs122b.idm.model.response.TokenResponse;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import helpers.CredentialsVerifier;
import helpers.TokenFormatVerifier;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    @PostMapping({"/register"})
    public ResponseEntity<Validate> register ( @RequestBody CredentialsRequest request ) {
        CredentialsVerifier.verifyEmailAndPassword(request.getPassword(), request.getEmail());
        return authManager.createAndInsertUser(request.getEmail(), request.getPassword());
    }

    @PostMapping({"/login"})
    public ResponseEntity<TokenResponse> login ( @RequestBody CredentialsRequest login ) throws JOSEException {
        // Verify the user exists/credentials are correct
        CredentialsVerifier.verifyEmailAndPassword(login.getPassword(), login.getEmail());

        // Retrieve the user information
        User user = authManager.selectAndAuthenticateUser(login.getEmail(), login.getPassword());

        // Check if user is locked
        if (user.getUserStatus().equals(UserStatus.LOCKED)) {
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        }

        // Check if user is banned
        if (user.getUserStatus().equals(UserStatus.BANNED)) {
            throw new ResultError(IDMResults.USER_IS_BANNED);
        }

        // Create access/refresh token
        String accessToken = jwtManager.buildAccessToken(user);
        RefreshToken refreshToken = jwtManager.buildRefreshToken(user);

        authManager.insertRefreshToken(refreshToken);

        TokenResponse response = new TokenResponse();
        response.setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping({"/refresh"})
    public ResponseEntity<TokenResponse> refresh ( @RequestBody RefreshTokenRequest refreshTokenString ) throws JOSEException {
        // Check if the token has correct length/format
        TokenFormatVerifier.verifyTokenFormat(refreshTokenString.getRefreshToken());

        // Verify access token
        RefreshToken refreshToken = authManager.verifyRefreshToken(refreshTokenString.getRefreshToken());

        // Check if token is expired
        if (jwtManager.hasExpired(refreshToken)) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        // Check if token is revoked
        else if (jwtManager.hasRevoked(refreshToken)) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }

        // Update refresh token status if needed
        else if (jwtManager.needsRefresh(refreshToken)) {
            authManager.expireRefreshToken(refreshToken);
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        // Update refresh token expire time
        jwtManager.updateRefreshTokenExpireTime(refreshToken);

        // Get the user from the refresh token
        User user = authManager.getUserFromRefreshToken(refreshToken);

        // Check if expire time is after max expire time
        if (refreshToken.getExpireTime().isAfter(refreshToken.getMaxLifeTime())) {
            // Update token status to revoked
            authManager.revokeRefreshToken(refreshToken);

            // Create a new refreshToken
            RefreshToken newRefreshToken = jwtManager.buildRefreshToken(user);

            // Insert new refresh token
            authManager.insertRefreshToken(newRefreshToken);

            // Create new access token
            return getTokenResponseResponseEntity(user, newRefreshToken);
        }

        else {
            // Update refreshToken expire time
            authManager.updateRefreshTokenExpireTime(refreshToken);

            // Create new access token
            return getTokenResponseResponseEntity(user, refreshToken);
        }
    }

    private ResponseEntity<TokenResponse> getTokenResponseResponseEntity(User user, RefreshToken newRefreshToken) throws JOSEException {
        String newAccessToken = jwtManager.buildAccessToken(user);

        TokenResponse response = new TokenResponse();
        response.setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN);
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping({"/authenticate"})
    public ResponseEntity<Validate> authenticate( @RequestBody AuthenticateRequest authenticate ) {
        jwtManager.verifyAccessToken(authenticate.getAccessToken());

        Validate response = new Validate();
        response.setResult(IDMResults.ACCESS_TOKEN_IS_VALID);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
