package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public User selectAndAuthenticateUser(String email, char[] password)
    {
        User user = repo.selectUser(email);

        String checkHashedPassword = Base64.getEncoder().encodeToString(hashPassword(password, user.getSalt()));

        // Correct Password
        if (user.getHashedPassword().equals(checkHashedPassword)) {
            return user;
        }

        // Incorrect Password
        throw new ResultError(IDMResults.INVALID_CREDENTIALS);

    }

    public ResponseEntity<Validate> createAndInsertUser(String email, char[] password)
    {
        byte[] salt = genSalt();
        byte[] hashedPassword = hashPassword(password, salt);

        User user = new User()
                .setEmail(email)
                .setUserStatus(UserStatus.ACTIVE)
                .setSalt(Base64.getEncoder().encodeToString(salt))
                .setHashedPassword(Base64.getEncoder().encodeToString(hashedPassword));

        return repo.insertUser(user);
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        repo.insertRefreshToken(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token)
    {
        return repo.getRefreshToken(token);
    }

    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        repo.updateRefreshTokenExpireTime(token, token.getExpireTime());
    }

    public void expireRefreshToken(RefreshToken token)
    {
        repo.setRefreshTokenStatus(token, 2);
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        repo.setRefreshTokenStatus(token, 3);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        return repo.getUserFromRefreshToken(refreshToken);
    }
}
