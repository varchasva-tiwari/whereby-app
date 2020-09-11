package com.demo.whereby.controller;

import com.demo.whereby.entity.Room;
import com.demo.whereby.entity.User;
import com.demo.whereby.model.RegistrationModel;
import com.demo.whereby.service.interfaces.RoomService;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;

@Controller
public class LoginController {

    //    public static Map<String, MyUser> users = new ConcurrentHashMap<>();
    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final int FREE_MEETINGS = 2;

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public String userRegistrationHandler(@ModelAttribute RegistrationModel registrationModel) {
        if (roomService.roomExists(registrationModel.getRoom())) {
            return "redirect:/registration?roomExists";
        }

        User user = new User();
        Room room = new Room();
        room.setName(registrationModel.getRoom());
        room.setUser(user);
        user.setEmail(registrationModel.getEmail());
        user.setName(registrationModel.getName());
        user.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
        user.setRole(registrationModel.getRole());
        user.setRooms(room);
        user.setCreatedAt(new Date());
        user.setActive(true);
        user.setMeetingsLeft(FREE_MEETINGS);

        userService.save(user);
        roomService.save(room);

        return "redirect:/registration?success";
    }

    @RequestMapping("/login")
    public String loginHandler() {
        return "login";
    }

    @RequestMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("user", new RegistrationModel());
        return "registration";
    }

    @RequestMapping("/")
    public String homePageHandler(HttpSession httpSession, Principal principal, Model model) {
        httpSession.setAttribute("loggedUser", principal.getName());
        model.addAttribute("username", principal.getName());
        return "dashboard";
    }

}