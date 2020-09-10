package com.demo.whereby.configurations;

import com.demo.whereby.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(getAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http.authorizeRequests()
                .antMatchers("/orders",
                        "/dashboard",
                        "/dashboard**",
                        "/dashboard/**",
                        "/dashboard*",
                        "/dashboard/*",
                        "/editProfile",
                        "/buy-meeting"
                ).authenticated()
                .regexMatchers(HttpMethod.GET, "/editProfile?userId=[0-9]+").authenticated()
                .antMatchers(
                        "/registerUser",
                        "/leaveSession**",
                        "/join-meeting/*",
                        "/registration",
                        "/session/**",
                        "/footer/**",
                        "/images/**",
                        "/customStyle.css",
                        "/style.css",
                        "/openvidu-browser-2.15.0.js"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll();
    }

    DaoAuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(getPasswordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
