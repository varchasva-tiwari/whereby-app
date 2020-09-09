package com.demo.whereby.controller;

import com.demo.whereby.entity.User;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping("/editProfile")
    public String getEditedProfile(@RequestParam("userId") int currentUserId, Model model) {
        model.addAttribute("currentUser", userService.findById(currentUserId));
        model.addAttribute("editedUser", new User());
        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String editProfile(@ModelAttribute("editedUser") User user, Model model) {
        User editedUser = userService.edit(user);

        model.addAttribute("currentUser", editedUser);

        return "redirect:/editProfile?userId="+user.getId();
    }

    @DeleteMapping("/deleteProfile")
    public String deleteProfile(@RequestParam("userId") int userId) {
        userService.deleteById(userId);

        return "redirect:/login";
    }
}
