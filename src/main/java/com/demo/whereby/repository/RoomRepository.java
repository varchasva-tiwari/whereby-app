package com.demo.whereby.repository;

import com.demo.whereby.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Integer> {

    Room findByName(String sessionName);
}
