package com.demo.whereby.controller;

import com.demo.whereby.entity.Room;
import com.demo.whereby.entity.User;
import com.demo.whereby.service.interfaces.RoomService;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @GetMapping(value = "/user-dashboard")
    public String goToDashboard() {
        return "userDashboard";
    }

    @PostMapping("/user-dashboard/delete-room")
    public String deleteRoom(@RequestParam("rid") Integer rid) {

        if (!isUserLoggedIn()) {
            return "redirect:/login";
        }

        roomService.delete(rid);

        String email = getLoggedInUser().getUsername();
        User user = userService.findByEmail(email);

        if (user.getRooms() != null) {
            for (int i = 0; i < user.getRooms().size(); i++) {
                if (user.getRooms().get(i).getId() == rid) {
                    user.getRooms().remove(i);
                    break;
                }
            }
            userService.save(user);
        }
        return "redirect:/user-dashboard?delete-success";
    }

    @PostMapping("/user-dashboard/create-room")
    public String createNewRoom(@RequestParam("roomName") String roomName) {

        if (!isUserLoggedIn()) {
            return "redirect:/login";
        }
        String email = getLoggedInUser().getUsername();
        User user = userService.findByEmail(email);

        Room room = new Room();
        room.setName(roomName);
        room.setUser(user);
        user.setRooms(room);

        Object result = userService.save(user);
        if (result != null) {
            result = roomService.save(room);
        }

        if (result != null) {
            return "redirect:/user-dashboard?room-success";
        } else {
            return "redirect:/user-dashboard?room-failure";
        }
    }

    @GetMapping(value = "/user-dashboard/join")
    public String joinExistingRoom(@RequestParam("rid") Long rid) {
        // send to process controller with pre-filled nickname and room
        return "";
    }

    // send redirect to profile controller
    // send redirect to logout controller
    // send redirect to about page
    // send redirect to contact us page


    boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication().
                getPrincipal() instanceof UserDetails;
    }

    UserDetails getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().
                getAuthentication().getPrincipal();

        return (UserDetails) principal;
    }

}
