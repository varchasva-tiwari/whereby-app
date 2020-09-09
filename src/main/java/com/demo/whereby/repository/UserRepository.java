package com.demo.whereby.repository;

import com.demo.whereby.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByEmail(String email);

    User findById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET profile_pic_name = :name WHERE id = :id", nativeQuery = true)
    void changeProfilePicName(@Param("id") int id, @Param("name") String name);

    @Query("SELECT u.profilePicName FROM User u WHERE u.id = :id")
    String getProfilePicName(@Param("id") int id);
}
