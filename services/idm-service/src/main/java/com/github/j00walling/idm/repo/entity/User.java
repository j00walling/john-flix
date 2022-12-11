package com.github.j00walling.idm.repo.entity;

import com.github.j00walling.idm.repo.entity.type.Role;
import com.github.j00walling.idm.repo.entity.type.UserStatus;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User
{
    private Integer    id;
    private String     email;
    private UserStatus userStatus;
    private String     salt;
    private String     hashedPassword;

    private List<Role> roles;

    public Integer getId()
    {
        return id;
    }

    public User setId(Integer id)
    {
        this.id = id;
        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public User setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public UserStatus getUserStatus()
    {
        return userStatus;
    }

    public User setUserStatus(UserStatus userStatus)
    {
        this.userStatus = userStatus;
        return this;
    }

    public String getSalt()
    {
        return salt;
    }

    public User setSalt(String salt)
    {
        this.salt = salt;
        return this;
    }

    public String getHashedPassword()
    {
        return hashedPassword;
    }

    public User setHashedPassword(String hashedPassword)
    {
        this.hashedPassword = hashedPassword;
        return this;
    }

    public List<Role> getRoles()
    {
        if (roles == null) {
            this.roles = new ArrayList<>();
        }

        return roles;
    }

    public User setRoles(Role... roles)
    {
        getRoles().addAll(Arrays.asList(roles));
        return this;
    }

    public User setRoles(List<Role> roles)
    {
        getRoles().addAll(roles);
        return this;
    }

    public User setRole(Role role)
    {
        getRoles().add(role);
        return this;
    }
}
