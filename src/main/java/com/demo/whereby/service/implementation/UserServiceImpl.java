package com.demo.whereby.service.implementation;

import com.demo.whereby.entity.User;
import com.demo.whereby.repository.UserRepository;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            throw new BadCredentialsException("user with this email doesnt exist");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),new ArrayList<GrantedAuthority>());
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String loggedUser) {
        return userRepository.findByEmail(loggedUser);
    }

    @Override
    public User edit(User user) {
        User editedUser = userRepository.findById(user.getId());

        if(user.getName() != null && user.getName().length() > 0) {
            editedUser.setName(user.getName());
        }

        if(user.getNickname() != null && user.getNickname().length() > 0) {
            editedUser.setNickname(user.getNickname());
        }

        if(user.getEmail() != null && user.getEmail().length() > 0) {
            editedUser.setEmail(user.getEmail());
        }

        if(user.getPassword() != null && user.getPassword().length() > 0) {
            editedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(editedUser);
    }

    @Override
    public User findById(int currentUserId) {
        return userRepository.findById(currentUserId);
    }

    @Override
    public void deleteById(int userId) {
        userRepository.findById(userId).setActive(false);
    }
}
