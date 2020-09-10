package com.demo.whereby.controller;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.demo.whereby.entity.Room;
import com.demo.whereby.entity.User;
import com.demo.whereby.service.interfaces.RoomService;
import com.demo.whereby.service.interfaces.UserService;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SessionController {

	@Autowired
	private UserService userService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private OpenVidu openVidu;

	@Resource(name = "mapSessions")
	private Map<String, Session> mapSessions;
	@Resource(name = "memberCounter")
	private Map<String,Integer> memberCounter;
	@Resource(name = "mapSessionNamesTokens")
	private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens;
	@Resource(name = "mapUserToToken")
	private Map<String, Map<String,String>> mapUserToToken;

	// Collection to pair session names and creation time
    private Map<String, Long> mapSessionTime = new ConcurrentHashMap<String, Long>();

	// Default Room Status
	private final boolean ROOM_STATUS = false;

	@RequestMapping(value = "/session" ,method = RequestMethod.POST)
	public String joinSession(@RequestParam("data") String clientData,
			@RequestParam("session-name") String sessionName, Model model, HttpSession httpSession){
		boolean owner = false;
		OpenViduRole role = OpenViduRole.PUBLISHER;
		Room room = roomService.findByRoom(sessionName);
		if(room == null){
			model.addAttribute("noRoom",true);
			return "dashboard";
		}
		User user = room.getUser();
		String userData = "guest";
		if(httpSession.getAttribute("loggedUser") != null){
			userData = (String) httpSession.getAttribute("loggedUser");
		}
		String serverData = "{\"serverData\": \"" + userData + "\"}";
		TokenOptions tokenOptions = new TokenOptions.Builder().data(serverData).role(role).build();
		String token = null;
		String userName = null;

		if(SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
			if(mapSessions.get(sessionName)!=null) {
				if(mapSessionNamesTokens.get(sessionName).containsKey(user.getEmail())){
					try {
						userData = "guest"+memberCounter.get(sessionName)+1;
						userName = userData;
						serverData = "{\"serverData\": \"" + userData + "\"}";
						tokenOptions = new TokenOptions.Builder().data(serverData).role(role).build();
						token = this.mapSessions.get(sessionName).generateToken(tokenOptions);
					} catch (OpenViduJavaClientException e) {
						model.addAttribute("error","unable to start session. retry..");
						return "dashboard";
					} catch (OpenViduHttpException e) {
						model.addAttribute("error","unable to start session. retry..");
						return "dashboard";
					}
					mapSessionNamesTokens.get(sessionName).put(token, role);
					mapSessionNamesTokens.get(sessionName).put(userData,role);
					memberCounter.put(sessionName,memberCounter.get(sessionName)+1);
				}else{
					model.addAttribute("meetingNotStartedByHost",true);
					return "dashboard";
				}
			}else{
				model.addAttribute("meetingNotStartedByHost",true);
				return "dashboard";
			}
		}else{
			userName = SecurityContextHolder.getContext().getAuthentication().getName();
			if(SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getEmail())){
				owner = true;
				try{
					if(mapSessions.get(sessionName)!=null) {
						if(mapSessionNamesTokens.get(sessionName).get(userName)==null){
							token = mapSessions.get(sessionName).generateToken(tokenOptions);
							mapSessionNamesTokens.get(sessionName).put(token, role);
							mapSessionNamesTokens.get(sessionName).put(userName,role);
							mapUserToToken.get(sessionName).put(userName,token);
							memberCounter.put(sessionName,memberCounter.get(sessionName)+1);
							userName = user.getEmail();
						}else{
							userName = user.getEmail();
							return "redirect:/leaveSession?userName="+userName+"&session-name="+sessionName;
						}
					} else {
						SessionProperties properties = new SessionProperties.Builder()
								.recordingMode(RecordingMode.MANUAL)// RecordingMode.ALWAYS for automatic recording
								.defaultOutputMode(Recording.OutputMode.COMPOSED)
								.defaultRecordingLayout(RecordingLayout.BEST_FIT)
								.build();
						Session session = this.openVidu.createSession(properties);
						token = session.generateToken(tokenOptions);
						mapSessions.put(sessionName, session);
						mapSessionNamesTokens.put(sessionName, new ConcurrentHashMap<>());
						mapSessionNamesTokens.get(sessionName).put(token, role);
						mapSessionNamesTokens.get(sessionName).put(userName,role);
						mapUserToToken.put(sessionName,new ConcurrentHashMap<>());
						mapUserToToken.get(sessionName).put(userName,token);
						memberCounter.put(sessionName,1);
						userName = SecurityContextHolder.getContext().getAuthentication().getName();
						Date date = new Date();
						this.mapSessionTime.put(sessionName,date.getTime());
					}
				}catch (Exception e){
					model.addAttribute("error","unable to start meeting");
					model.addAttribute("username",userName);
					e.printStackTrace();
					return "dashboard";
				}

			}else{
				if(mapSessions.get(sessionName)!=null){
					if(mapSessionNamesTokens.get(sessionName).containsKey(user.getEmail())){
						try{
							token = this.mapSessions.get(sessionName).generateToken(tokenOptions);
						}catch(Exception e){
							model.addAttribute("error","unable to start meeting");
							model.addAttribute("username",userName);
							return "dashboard";
						}
						mapSessionNamesTokens.get(sessionName).put(token, role);
						mapSessionNamesTokens.get(sessionName).put(userName,role);
						memberCounter.put(sessionName,memberCounter.get(sessionName)+1);
						mapUserToToken.get(sessionName).put(userName,token);
					}else{
						model.addAttribute("meetingNotStartedByHost",true);
						model.addAttribute("username",userName);
						return "dashboard";
					}
				}else{
					model.addAttribute("meetingNotStartedByHost",true);
					model.addAttribute("username",userName);
					return "dashboard";
				}
			}
		}
		model.addAttribute("owner",owner);
		model.addAttribute("sessionName", sessionName);
		model.addAttribute("token", token);
		model.addAttribute("nickName", clientData);
		model.addAttribute("userName", userName);
		model.addAttribute("currentUserId", user.getId());
		model.addAttribute("locked", ROOM_STATUS);
		model.addAttribute("startTime",mapSessionTime.get(sessionName));
		System.out.println(sessionName);
		return "session";
	}
//, method = RequestMethod.POST
	@RequestMapping(value = "/leaveSession")
	public String removeUser(@RequestParam(name = "session-name",required = false) String sessionName,@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "token",required = false) String token, Model model, HttpSession httpSession) throws Exception {
		if(token == null){
			if(mapUserToToken.containsKey(sessionName)){
				if(mapUserToToken.get(sessionName).containsKey(userName)){
					token = mapUserToToken.get(sessionName).get(userName);
					mapUserToToken.get(sessionName).remove(userName);
				}
			}
		}

		if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

			// If the token exists
			if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
				this.mapSessionNamesTokens.get(sessionName).remove(userName);
				memberCounter.put(sessionName,memberCounter.get(sessionName)-1);
				// User left the session
				if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
					// Last user left: session must be removed
					this.mapSessions.remove(sessionName);
				}
				model.addAttribute("left",true);
				model.addAttribute("username",SecurityContextHolder.getContext().getAuthentication().getName());
				return "dashboard";

			} else {
				// The TOKEN wasn't valid
				System.out.println("Problems in the app server: the TOKEN wasn't valid");
				model.addAttribute("username",SecurityContextHolder.getContext().getAuthentication().getName());
				return "dashboard";
			}

		} else {
			// The SESSION does not exist
			System.out.println("Problems in the app server: the SESSION does not exist");
			model.addAttribute("username",SecurityContextHolder.getContext().getAuthentication().getName());
			return "dashboard";
		}
	}

	private void checkUserLogged(HttpSession httpSession) throws Exception {
		if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
			throw new Exception("User not logged");
		}
	}

	@RequestMapping("/join-meeting/{room}")
	public String joinWithLink(@PathVariable("room") String room,Model model){
		model.addAttribute("guest",room);
		return "dashboard";
	}
}



//https://localhost:5000/join-meeting/ram

//
//window.addEventListener('beforeunload', function (e) {
//		e.preventDefault();
//		e.returnValue = '';
//		})


