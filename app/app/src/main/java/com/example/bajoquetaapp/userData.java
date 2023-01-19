package com.example.bajoquetaapp;

public class userData {
    private String userUUID, name, email;
    private Boolean led;

    public userData() {
    }

    public userData(String userUUID, String name, String email, Boolean led) {
        this.userUUID = userUUID;
        this.name = name;
        this.email = email;
        this.led = led;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

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

    public Boolean getLed() {
        return led;
    }

    public void setLed(Boolean led) {
        this.led = led;
    }

    @Override
    public String toString() {
        return "userData{" +
                "userUUID='" + userUUID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", led=" + led +
                '}';
    }
}
