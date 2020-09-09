package com.demo.whereby.service.implementation;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class UserServiceImpl implements UserService {
    AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARCOFU6RC7CT7HONM",
            "/L6CDMEm9U/nZKqqWXUgIfjV2HQpCq2OOAXLlsIk");

    AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build();

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

        System.out.println(user.getId());
        System.out.println(editedUser.getId());

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

    @Override
    public void changeProfilePicName(int id, String name) {
        userRepository.changeProfilePicName(id, name);
    }

    @Override
    public String getProfilePic(int userId) throws IOException {
        S3Object file = s3Client.getObject("whereby-bucket", userRepository.getProfilePicName(userId));

        byte[] byteArray = IOUtils.toByteArray(file.getObjectContent());

        if(byteArray.length > 0) {
            return Base64.getEncoder().encodeToString(byteArray);
        } else {
            return "";
        }
    }
}
