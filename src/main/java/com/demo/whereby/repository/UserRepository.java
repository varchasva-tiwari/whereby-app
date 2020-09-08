package com.demo.whereby.repository;

import com.demo.whereby.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByEmail(String email);

    User findById(int id);
}
