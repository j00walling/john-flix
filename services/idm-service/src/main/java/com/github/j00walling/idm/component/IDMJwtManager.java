package com.github.j00walling.idm.component;

import com.github.j00walling.idm.config.IDMServiceConfig;
import com.github.j00walling.idm.repo.entity.User;
//import com.github.klefstad_teaching.cs122b.core.error.ResultError;
//import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
//import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.j00walling.idm.repo.entity.RefreshToken;
import com.github.j00walling.idm.repo.entity.type.TokenStatus;
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jose.JWSHeader;
//import com.nimbusds.jose.JWSVerifier;
//import com.nimbusds.jose.proc.BadJOSEException;
//import com.nimbusds.jwt.JWTClaimsSet;
//import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Component
public class IDMJwtManager
{
//    private final JWTManager jwtManager;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
//        this.jwtManager =
//            new JWTManager.Builder()
//                .keyFileName(serviceConfig.keyFileName())
//                .accessTokenExpire(serviceConfig.accessTokenExpire())
//                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
//                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
//                .build();
    }

//    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
//        throws JOSEException
//    {
//        JWSHeader header =
//                new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
//                        .keyID(jwtManager.getEcKey().getKeyID())
//                        .type(JWTManager.JWS_TYPE)
//                        .build();
//
//        return new SignedJWT(header, claimsSet);
//    }

//    public String buildAccessToken(User user) throws JOSEException {
//        JWTClaimsSet claims = new JWTClaimsSet.Builder()
//                .subject(user.getEmail())
//                .expirationTime(
//                        Date.from(
//                                Instant.now().plus(this.jwtManager.getAccessTokenExpire())))
//                .issueTime(Date.from(Instant.now()))
//                .claim(JWTManager.CLAIM_ROLES, user.getRoles())
//                .claim(JWTManager.CLAIM_ID, user.getId())
//                .build();
//
//        SignedJWT signedJWT = buildAndSignJWT(claims);
//        signedJWT.sign(jwtManager.getSigner());
//
//        return signedJWT.serialize();
//    }

//    public void verifyAccessToken(String jws) {
//        try {
//            SignedJWT accessToken = SignedJWT.parse(jws);
//            if (!accessToken.verify(jwtManager.getVerifier())) throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
//            JWTClaimsSet claimsSet = jwtManager.getJwtProcessor().process(accessToken, null);
//
//            if (Date.from(Instant.now()).after(claimsSet.getExpirationTime())) {
//                throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
//            }
//        }
//        catch (ParseException | JOSEException | BadJOSEException e) {
//            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
//        }
//    }

//    public RefreshToken buildRefreshToken(User user)
//    {
//        return new RefreshToken()
//                .setToken(generateUUID().toString())
//                .setTokenStatus(TokenStatus.ACTIVE)
//                .setUserId(user.getId())
//                .setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()))
//                .setMaxLifeTime(Instant.now().plus(this.jwtManager.getMaxRefreshTokenLifeTime()));
//
//    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        return (refreshToken.getTokenStatus().equals(TokenStatus.EXPIRED)) ?  true :  false;
    }

    public boolean hasRevoked(RefreshToken refreshToken) {
        return (refreshToken.getTokenStatus().equals(TokenStatus.REVOKED)) ?  true :  false;
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        // Check if current time is after expire time
        if (Instant.now().compareTo(refreshToken.getExpireTime()) == 0 || Instant.now().compareTo(refreshToken.getExpireTime()) > 0) { return true; }

        // Check if current time is after max expire time
        if (Instant.now().compareTo(refreshToken.getMaxLifeTime()) == 0 || Instant.now().compareTo(refreshToken.getMaxLifeTime()) > 0) { return true; }

        return false;
    }

//    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
//    {
//        refreshToken.setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()));
//    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
