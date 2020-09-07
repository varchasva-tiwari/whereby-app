package com.demo.whereby.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/user-dashboard")
public class DashboardController {

    @GetMapping(value = "/")
    public String goToDashboard(){
        return "userDashboard";
    }

    @PostMapping(value = "/delete-room")
    public String deleteRoom(@RequestParam("rid") Long rid){
        // delete room using room id
        return "redirect:/user-dashboard?delete-success";
    }

    @PostMapping(value = "/create-room")
    public String createNewRoom(@RequestParam("roomName") String roomName){
        // create new room using roomName
        return "redirect:/user-dashboard?room-success";
    }

    @GetMapping(value = "/join")
    public String joinExistingRoom(@RequestParam("rid") Long rid){
        // send to process controller with pre-filled nickname and room
        return "";
    }

    // send redirect to profile controller
    // send redirect to logout controller
    // send redirect to about page
    // send redirect to contact us page
}
