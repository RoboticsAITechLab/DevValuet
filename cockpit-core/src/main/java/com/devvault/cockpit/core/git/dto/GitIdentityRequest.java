package com.devvault.cockpit.core.git.dto;

public class GitIdentityRequest {
    private String name;
    private String email;

    public GitIdentityRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
