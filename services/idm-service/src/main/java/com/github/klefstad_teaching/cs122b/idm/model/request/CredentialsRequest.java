package com.github.klefstad_teaching.cs122b.idm.model.request;

public class CredentialsRequest {
    private String email;
    private char[] password;

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }
}
