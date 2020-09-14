package com.demo.whereby.controller;

import com.demo.whereby.entity.User;
import com.demo.whereby.service.interfaces.RoomService;
import com.demo.whereby.service.interfaces.UserService;
import com.demo.whereby.utility.SessionUtility;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class SessionController {

    private final boolean ROOM_STATUS = false;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private OpenVidu openVidu;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SessionUtility utility;
    @Resource(name = "mapSessions")
    private Map<String, Session> mapSessions;
    @Resource(name = "memberCounter")
    private Map<String, Integer> memberCounter;
    @Resource(name = "mapSessionNamesTokens")
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens;
    @Resource(name = "mapUserToToken")
    private Map<String, Map<String, String>> mapUserToToken;
    private Map<String, Long> mapSessionTime = new ConcurrentHashMap<String, Long>();

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public String joinSession(@RequestParam("data") String clientData,
                              @RequestParam("session-name") String sessionName,
                              Model model, HttpSession httpSession) {
        boolean owner = false;
        OpenViduRole role = OpenViduRole.PUBLISHER;
        if (!utility.roomExist(sessionName)) {
            model.addAttribute("noRoom", true);
            return "dashboard";
        }
        User roomOwner = utility.getOwner(sessionName);
        String userData = "guest";
        if (httpSession.getAttribute("loggedUser") != null) {
            userData = (String) httpSession.getAttribute("loggedUser");
        }

        if (httpSession.getAttribute("googleLoggedUser") != null) {
            SecurityContext context = SecurityContextHolder.getContext();
            User googleUser = null;

            googleUser = userService.findByEmail(httpSession.getAttribute("googleLoggedUser").toString());

            if (googleUser == null) {
                googleUser = new User();
                googleUser.setName((String) context.getAuthentication().getPrincipal());
                googleUser.setEmail((String) context.getAuthentication().getPrincipal());
                googleUser.setPassword(passwordEncoder.encode((String) context.getAuthentication().getCredentials()));
                googleUser.setCreatedAt(new Date());
                String authorities = context.getAuthentication().getAuthorities().toString();
                googleUser.setRole(context.getAuthentication().getAuthorities().toString().substring(1, authorities.length() - 1));

                googleUser = userService.save(googleUser);

                userData = googleUser.getEmail();
            }
        }

        String serverData = "{\"serverData\": \"" + userData + "\"}";
        TokenOptions tokenOptions = new TokenOptions.Builder().data(serverData).role(role).build();
        String token = null;
        String userName = null;

        if (!utility.isUserLoggedin()) {
            if (sessionExist(sessionName)) {
                if (mapSessionNamesTokens.get(sessionName).containsKey(roomOwner.getEmail())) {
                    try {
                        userData = "guest" + memberCounter.get(sessionName) + 1;
                        userName = userData;
                        serverData = "{\"serverData\": \"" + userData + "\"}";
                        tokenOptions = new TokenOptions.Builder().data(serverData).role(role).build();
                        getTokenAndSetPropsForExistingSession(sessionName, tokenOptions, role, userName);
                    } catch (Exception e) {
                        model.addAttribute("error", "unable to start session. retry..");
                        return "dashboard";
                    }
                } else {
                    model.addAttribute("meetingNotStartedByHost", true);
                    return "dashboard";
                }
            } else {
                model.addAttribute("meetingNotStartedByHost", true);
                return "dashboard";
            }
        } else {
            userName = utility.getUserName();
            if (utility.isOwner(userName, sessionName)) {
                owner = true;
                try {
                    if (sessionExist(sessionName)) {
                        if (!isUserAlreadyPresesnt(sessionName, userName)) {
                            token = getTokenAndSetPropsForExistingSession(sessionName, tokenOptions, role, userName);
                        } else {
                            return "redirect:/leaveSession?userName=" + userName + "&session-name=" + sessionName;
                        }
                    } else {
                        if (utility.checkMeetingsAvailable(userName)) {
                            return "redirect:/user-dashboard?buy";
                        } else {
                            User loggedUser = utility.getLoggedInUser();
                            loggedUser.setMeetingsLeft(loggedUser.getMeetingsLeft() - 1);
                            userService.save(loggedUser);
                        }
                        SessionProperties properties = getSessionProps();
                        Session session = this.openVidu.createSession(properties);
                        token = getTokenAndSetPropsForNewSession(session, tokenOptions, sessionName, role, userName);
                    }
                } catch (Exception e) {
                    model.addAttribute("error", "unable to start meeting");
                    model.addAttribute("username", userName);
                    e.printStackTrace();
                    return "dashboard";
                }
            } else {
                if (sessionExist(sessionName)) {
                    if (isOwnerInRoom(sessionName, roomOwner)) {
                        try {
                            getTokenAndSetPropsForExistingSession(sessionName, tokenOptions, role, userName);
                        } catch (Exception e) {
                            model.addAttribute("error", "unable to start meeting");
                            model.addAttribute("username", userName);
                            return "dashboard";
                        }
                    } else {
                        model.addAttribute("meetingNotStartedByHost", true);
                        model.addAttribute("username", userName);
                        return "dashboard";
                    }
                } else {
                    model.addAttribute("meetingNotStartedByHost", true);
                    model.addAttribute("username", userName);
                    return "dashboard";
                }
            }
        }
        model.addAttribute("owner", owner);
        model.addAttribute("sessionName", sessionName);
        model.addAttribute("token", token);
        model.addAttribute("nickName", clientData);
        model.addAttribute("userName", userName);
        model.addAttribute("currentUserId", roomOwner.getId());
        model.addAttribute("locked", ROOM_STATUS);
        model.addAttribute("startTime", mapSessionTime.get(sessionName));
        System.out.println(sessionName);
        return "session";
    }

    @RequestMapping(value = "/leaveSession")
    public String removeUser(@RequestParam(name = "session-name", required = false) String sessionName, @RequestParam(name = "userName", required = false) String userName,
                             @RequestParam(name = "token", required = false) String token, Model model, HttpSession httpSession) throws Exception {
        if (token == null) {
            if (mapUserToToken.containsKey(sessionName)) {
                if (mapUserToToken.get(sessionName).containsKey(userName)) {
                    token = mapUserToToken.get(sessionName).get(userName);
                    mapUserToToken.get(sessionName).remove(userName);
                }
            }
        }

        if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

            if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
                this.mapSessionNamesTokens.get(sessionName).remove(userName);
                memberCounter.put(sessionName, memberCounter.get(sessionName) - 1);
                if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
                    // Last user left: session must be removed
                    this.mapSessions.remove(sessionName);
                }
                model.addAttribute("left", true);
                model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
                return "dashboard";

            } else {
                System.out.println("Problems in the app server: the TOKEN wasn't valid");
                model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
                return "dashboard";
            }

        } else {
            System.out.println("Problems in the app server: the SESSION does not exist");
            model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
            return "dashboard";
        }
    }

    private void checkUserLogged(HttpSession httpSession) throws Exception {
        if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
            throw new Exception("User not logged");
        }
    }

    @RequestMapping("/join-meeting/{room}")
    public String joinWithLink(@PathVariable("room") String room, Model model) {
        model.addAttribute("guest", room);
        return "dashboard";
    }

    public boolean sessionExist(String sessionName) {
        return mapSessions.get(sessionName) == null ? false : true;
    }

    public boolean isUserAlreadyPresesnt(String sessionName, String userName) {
        return mapSessionNamesTokens.get(sessionName).get(userName) == null ? false : true;
    }

    public String getTokenAndSetPropsForExistingSession(String sessionName, TokenOptions tokenOptions, OpenViduRole role, String userName) throws OpenViduJavaClientException, OpenViduHttpException {
        String token = mapSessions.get(sessionName).generateToken(tokenOptions);
        mapSessionNamesTokens.get(sessionName).put(token, role);
        mapSessionNamesTokens.get(sessionName).put(userName, role);
        mapUserToToken.get(sessionName).put(userName, token);
        memberCounter.put(sessionName, memberCounter.get(sessionName) + 1);
        return token;
    }

    public String getTokenAndSetPropsForNewSession(Session session, TokenOptions tokenOptions, String sessionName, OpenViduRole role, String userName) throws OpenViduJavaClientException, OpenViduHttpException {
        String token = session.generateToken(tokenOptions);
        mapSessions.put(sessionName, session);
        mapSessionNamesTokens.put(sessionName, new ConcurrentHashMap<>());
        mapSessionNamesTokens.get(sessionName).put(token, role);
        mapSessionNamesTokens.get(sessionName).put(userName, role);
        mapUserToToken.put(sessionName, new ConcurrentHashMap<>());
        mapUserToToken.get(sessionName).put(userName, token);
        memberCounter.put(sessionName, 1);
        Date date = new Date();
        this.mapSessionTime.put(sessionName, date.getTime());
        return token;
    }

    public SessionProperties getSessionProps() {
        return new SessionProperties.Builder()
                .recordingMode(RecordingMode.MANUAL)
                .defaultOutputMode(Recording.OutputMode.COMPOSED)
                .defaultRecordingLayout(RecordingLayout.BEST_FIT)
                .build();
    }

    public boolean isOwnerInRoom(String sessionName, User roomOwner) {
        return mapSessionNamesTokens.get(sessionName).containsKey(roomOwner.getEmail());
    }

}

