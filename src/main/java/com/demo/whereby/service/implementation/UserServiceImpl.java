package com.demo.whereby.service.implementation;

import com.demo.whereby.entity.User;
import com.demo.whereby.repository.UserRepository;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
}
