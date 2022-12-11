package com.github.j00walling.idm.model.request;

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
