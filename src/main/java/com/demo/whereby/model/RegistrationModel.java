package com.demo.whereby.model;

public class RegistrationModel {
    public String name;
    public String email;
    public String password;
    public String role;
    public String room;

    public RegistrationModel(String name, String email, String password, String role, String room) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.room = room;
    }

    public RegistrationModel() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
