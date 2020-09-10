package com.demo.whereby.service.interfaces;

import com.demo.whereby.entity.Room;

public interface RoomService {
    Room save(Room room);

    void delete(Integer rid);

    boolean roomExists(String name);

    Room findByRoom(String sessionName);
}
