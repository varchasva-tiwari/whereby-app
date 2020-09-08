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

    //    public static Map<String, MyUser> users = new ConcurrentHashMap<>();
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    public LoginController() {
//        users.put("publisher1", new MyUser("publisher1", "pass", OpenViduRole.PUBLISHER));
//        users.put("publisher2", new MyUser("publisher2", "pass", OpenViduRole.PUBLISHER));
//        users.put("subscriber", new MyUser("subscriber", "pass", OpenViduRole.SUBSCRIBER));
//    }

//    @RequestMapping(value = "/")
//    public String logout(HttpSession httpSession) {
//        if (checkUserLogged(httpSession)) {
//            return "redirect:/dashboard";
//        } else {
//            httpSession.invalidate();
//            return "index";
//        }
//    }

//    @RequestMapping(value = "/dashboard", method = {RequestMethod.GET, RequestMethod.POST})
//    public String login(@RequestParam(name = "user", required = false) String user,
//                        @RequestParam(name = "pass", required = false) String pass, Model model, HttpSession httpSession) {
//
//        // Check if the user is already logged in
//        String userName = (String) httpSession.getAttribute("loggedUser");
//        if (userName != null) {
//            // User is already logged. Immediately return dashboard
//            model.addAttribute("username", userName);
//            return "dashboard";
//        }
//
//        // User wasn't logged and wants to
//        if (login(user, pass)) { // Correct user-pass
//
//            // Validate session and return OK
//            // Value stored in HttpSession allows us to identify the user in future requests
//            httpSession.setAttribute("loggedUser", user);
//            model.addAttribute("username", user);
//
//            // Return dashboard.html template
//            return "dashboard";
//
//        } else { // Wrong user-pass
//            // Invalidate session and redirect to index.html
//            httpSession.invalidate();
//            return "redirect:/";
//        }
//    }

//    @RequestMapping(value = "/logout", method = RequestMethod.POST)
//    public String logout(Model model, HttpSession httpSession) {
//        httpSession.invalidate();
//        return "redirect:/";
//    }
//
//    private boolean login(String user, String pass) {
//        return (user != null && pass != null && users.containsKey(user) && users.get(user).pass.equals(pass));
//    }
//
//    private boolean checkUserLogged(HttpSession httpSession) {
//        return !(httpSession == null || httpSession.getAttribute("loggedUser") == null);
//    }

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