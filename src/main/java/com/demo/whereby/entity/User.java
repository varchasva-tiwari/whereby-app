package com.demo.whereby.entity;

import javax.persistence.*;
import java.net.URL;
import java.util.Date;
import io.openvidu.java.client.OpenViduRole;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_pic_name")
    private String profilePicName;

    @Column(name = "meetings_left")
    private int meetingsLeft;

    @Column(name = "role")
    private String role;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "active")
    private boolean active = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getMeetingsLeft() {
        return meetingsLeft;
    }

    public void setMeetingsLeft(int meetingsLeft) {
        this.meetingsLeft = meetingsLeft;
    }

    public List<Room> getRooms() {
        return new ArrayList<Room>(this.rooms);
    }

    public void setRooms(Room room) {
        this.rooms.add(room);
    }
}
