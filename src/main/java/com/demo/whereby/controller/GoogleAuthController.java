package com.demo.whereby.controller;

import com.google.api.client.googleapis.auth.oauth2.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GoogleAuthController {

    private final GoogleAuthorizationCodeFlow authorizationCodeFlow;

    public GoogleAuthController(GoogleAuthorizationCodeFlow authorizationCodeFlow) {
        this.authorizationCodeFlow = authorizationCodeFlow;
    }

    @GetMapping("/google/login")
    public RedirectView redirectToGoogle() {
        GoogleAuthorizationCodeRequestUrl requestUrl =
                authorizationCodeFlow.newAuthorizationUrl();

        requestUrl.setRedirectUri("https://localhost:5000/callback");

        String redirectUrl = requestUrl.build();

        return new RedirectView(redirectUrl);
    }

    @GetMapping("/callback")
    public RedirectView authCallback(@RequestParam String code, HttpServletRequest servletRequest) throws IOException {
        GoogleAuthorizationCodeTokenRequest tokenRequest =
                authorizationCodeFlow.newTokenRequest(code);

        tokenRequest.setRedirectUri("https://localhost:5000/callback");

        GoogleTokenResponse tokenResponse = tokenRequest.execute();

        GoogleIdToken idToken = tokenResponse.parseIdToken();

        setSessionCookie(servletRequest, tokenResponse);

        return new RedirectView("/");
    }

    private void setSessionCookie(HttpServletRequest request, GoogleTokenResponse tokenResponse) throws IOException {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("publisher"));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                tokenResponse.parseIdToken().getPayload().getEmail(), "pass", authorities);

        SecurityContext context = SecurityContextHolder.getContext();

        context.setAuthentication(token);

        HttpSession session = request.getSession(true);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute("googleLoggedUser", context.getAuthentication().getPrincipal());
    }
}
