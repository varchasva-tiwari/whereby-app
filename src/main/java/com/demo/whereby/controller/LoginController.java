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

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/registerUser",method = RequestMethod.POST)
    public String userRegistrationHandler(@ModelAttribute RegistrationModel registrationModel) {
        User user = new User();
        Room room = new Room();
        room.setName(registrationModel.getRoom());
        room.setUser(user);
        user.setEmail(registrationModel.getEmail());
        user.setName(registrationModel.getName());
        user.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
        user.setRole(registrationModel.getRole());
        user.setRooms(room);
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
        model.addAttribute("user",new RegistrationModel());
        return "registration";
    }

    @RequestMapping("/")
    public String homePageHandler(HttpSession httpSession, Principal principal, Model model) {
        httpSession.setAttribute("loggedUser", principal.getName());
        model.addAttribute("username", principal.getName());
        return "dashboard";
    }

//    public class MyUser {
//
//        String name;
//        String pass;
//        OpenViduRole role;
//
//        public MyUser(String name, String pass, OpenViduRole role) {
//            this.name = name;
//            this.pass = pass;
//            this.role = role;
//        }
//    }

}