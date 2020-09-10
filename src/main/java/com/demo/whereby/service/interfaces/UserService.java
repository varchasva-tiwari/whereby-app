package com.demo.whereby.service.interfaces;

import com.demo.whereby.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

public interface UserService extends UserDetailsService {

    com.demo.whereby.entity.User save(com.demo.whereby.entity.User user);

    User findByEmail(String loggedUser);

    User edit(User user);

    User findById(int currentUserId);

    void deleteById(int userId);

    void changeProfilePicName(int id, String name);

    String getProfilePic(int userId) throws IOException;

    boolean hasProfilePic(int userId);
}
