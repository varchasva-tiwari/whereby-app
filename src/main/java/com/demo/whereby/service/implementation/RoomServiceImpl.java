package com.demo.whereby.service.implementation;


import com.demo.whereby.entity.Room;
import com.demo.whereby.repository.RoomRepository;
import com.demo.whereby.service.interfaces.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void delete(Integer rid) {
        roomRepository.deleteById(rid);
    }

    @Override
    public boolean roomExists(String name) {
        boolean result = false;
        List<Room> rooms = roomRepository.findAll();
        if(rooms != null){
            for(Room room:rooms){
                if(room.getName().equalsIgnoreCase(name)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    public Room findByRoom(String sessionName) {
        return roomRepository.findByName(sessionName);
    }
}
