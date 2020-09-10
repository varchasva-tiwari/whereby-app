package com.demo.whereby.controller;

import com.demo.whereby.entity.User;
import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping("/editProfile")
    public String getEditedProfile(@RequestParam("userId") int currentUserId,
                                   @ModelAttribute("pic") String pic,
                                   @ModelAttribute("fileName") String profilePicName,
                                   Model model) throws IOException {
        String profilePic = null;

        if(profilePicName != null && profilePicName.length() > 0) {
            userService.changeProfilePicName(currentUserId, profilePicName);
        }

        if(userService.hasProfilePic(currentUserId)) {
            profilePic = userService.getProfilePic(currentUserId);
        } else {
            profilePic = pic;
        }

        model.addAttribute("currentUser", userService.findById(currentUserId));
        model.addAttribute("editedUser", new User());
        model.addAttribute("pic", profilePic);

        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String editProfile(@ModelAttribute("editedUser") User user, Model model) {
        User editedUser = userService.edit(user);

        model.addAttribute("currentUser", editedUser);

        return "redirect:/editProfile?userId="+user.getId();
    }

    @PostMapping("/deleteProfile")
    public String deleteProfile(@RequestParam("userId") int userId) {
        userService.deleteById(userId);

        return "redirect:/login";
    }
}
