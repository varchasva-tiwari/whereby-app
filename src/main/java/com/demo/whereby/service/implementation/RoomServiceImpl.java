package com.demo.whereby.service.implementation;


import com.demo.whereby.entity.Room;
import com.demo.whereby.repository.RoomRepository;
import com.demo.whereby.service.interfaces.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }
}
