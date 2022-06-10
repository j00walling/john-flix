package com.github.klefstad_teaching.cs122b.movies.util;

import java.util.List;

public class VerifyAdmin {
    public static boolean verify(List<String> roles) {
        for (String role : roles) {
            if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("employee"))
                return true;
        }
        return false;
    }
}
