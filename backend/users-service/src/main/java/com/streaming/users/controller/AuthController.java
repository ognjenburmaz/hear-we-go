package com.streaming.users.controller;

import com.streaming.common.dto.AuthRequest;
import com.streaming.common.dto.UserRegistrationRequest;
import com.streaming.users.model.User;
import com.streaming.users.security.TokenUtils;
import com.streaming.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

//    private final UserService userService;

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    //    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
//        String token = userService.login(request.getUsername(), request.getPassword());
//        return ResponseEntity.ok(token);
//    }
    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<TokenUtils.JwtDTO> createAuthenticationToken(
            @RequestBody AuthRequest authenticationRequest, HttpServletResponse response, HttpSession session) {
        System.out.println("OKINUO SE LOGIN CONTROLLER! 1");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        System.out.println("OKINUO SE LOGIN CONTROLLER! 2");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("OKINUO SE LOGIN CONTROLLER! 3");

        UserDetails user = (UserDetails) authentication.getPrincipal();

        Optional<User> loggedInUser = userService.findByUsername(user.getUsername());
        System.out.println("ULOGOVAN" + loggedInUser.get().getUsername());
        session.setAttribute("korisnik", loggedInUser);

        String jwt = tokenUtils.generateToken(user);
        int expiresIn = tokenUtils.getExpiredIn();
        System.out.println("OKINUO SE LOGIN CONTROLLER! 4");
        System.out.println("TOKEN: " + jwt);

        return ResponseEntity.ok(new TokenUtils.JwtDTO(jwt, expiresIn));
    }

    @CrossOrigin
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

}