package com.demo.whereby.utility;


import com.demo.whereby.entity.Room;
import com.demo.whereby.entity.User;
import com.demo.whereby.service.interfaces.RoomService;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class SessionUtility {

    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;

    public boolean isUserLoggedin() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    public String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isOwner(String userName, String sessionName) {
        User user = userService.findByEmail(userName);
        Room room = roomService.findByRoom(sessionName);
        return room.getUser().getEmail().equals(user.getEmail());
    }

    public boolean userExist(String userName) {
        User user = userService.findByEmail(userName);
        if (user != null) {
            return true;
        }
        return false;
    }

    public boolean checkMeetingsAvailable(String userName) {
        User user = userService.findByEmail(userName);
        return user.getMeetingsLeft() <= 0 ? false : true;
    }

    public User getLoggedInUser() {
        return userService.findByEmail(getUserName());
    }

    public boolean roomExist(String sessionName){
        return getRoom(sessionName)==null?false:true;
    }

    public Room getRoom(String sessioName){
        return roomService.findByRoom(sessioName);
    }

    public User getOwner(String sessionName) {
        return getRoom(sessionName).getUser();
    }
}
