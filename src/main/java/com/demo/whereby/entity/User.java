package com.demo.whereby.entity;

import io.openvidu.java.client.OpenViduRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;
    public String email;
    public String password;
    public String role;
    @OneToMany(mappedBy = "user")
    public List<Room> rooms = new ArrayList<Room>();

    public User() {
    }

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Room> getRooms() {
        return new ArrayList<Room>(this.rooms);
    }

    public void setRooms(Room room) {
        this.rooms.add(room);
    }


}
