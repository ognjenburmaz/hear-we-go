package com.streaming.users.service;

import com.streaming.common.dto.UserRegistrationRequest;
import com.streaming.users.model.User;
import com.streaming.users.repository.UserRepository;
import com.streaming.users.security.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils jwtUtil;
//    private final AuthenticationManager authenticationManager;

    public User registerUser(UserRegistrationRequest request) {
        // TODO: Check if user exists...

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // HASH THE PASSWORD!
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setRole("RK");
        user.setActive(true); // Temporary true for testing
        user.setLastPasswordReset(LocalDateTime.now());

        return userRepository.save(user);
    }

//    public String login(String username, String password) {
//        // This attempts to authenticate against the DB using BCrypt
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//        );
//
//        // If we get here, password is correct. Fetch role and generate token.
//        User user = userRepository.findByUsername(username).orElseThrow();
//        return jwtUtil.generateToken(user.getUsername(), user.getRole());
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("There is no user with username " + username);
        } else {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            String role = "RK";

//            for (User u : userRepository.findAll()) {
//                if (user.get().getUsername().equals(u.getUsername())) {
//                    role = "RK";
//                    break;
//                }
//            }

//            for (Administrator a : userService.findAllAdmins()) { // TODO
//                if (user.getEmail().equals(a.getEmail())) {
//                    role = "ROLE_ADMINISTRATOR";
//                    break;
//                }
//            }

            grantedAuthorities.add(new SimpleGrantedAuthority(role));

            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername().trim(),
                    user.get().getPassword().trim(),
                    grantedAuthorities);
        }
    }

//    public User findByEmail(String email) {
//        Optional<User> user = userRepository.findFirstByEmail(email);
//        if (!user.isEmpty()) {
//            return user.get();
//        }
//        return null;
//    }

    public User createUser(User user) {

//        Optional<User> user = userRepository.findFirstByEmail(userDTO.getEmail());
//
//        if (user.isPresent()) {
//            return null;
//        }
//
//        User newUser = new User();
//        newUser.setEmail(userDTO.getEmail());
//        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        newUser.setCreatedAt(LocalDate.now());
//        newUser.setAddress(userDTO.getAddress());
//        newUser.setBirthday(userDTO.getBirthday());
//        newUser.setCity(userDTO.getCity());
//        newUser.setName(userDTO.getName());
//        newUser.setPhoneNumber(userDTO.getPhone_number());
//        newUser.setImageFilename("placeholder.jpg");
//
////        newUser.setRole(Roles.USER);
//        newUser = userRepository.save(newUser);
//
//        return newUser;
        return null; // TODO
    }

//    public User createUser(User user) {
//        return userRepository.save(user);
//    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public List<User> findAllUsers() {
        return this.userRepository.findAll();
    }

//    public List<Administrator> findAllAdmins() {
//        return this.userRepository.findAllAdmins();
//    }

    public User save(User forEdit) {
        return userRepository.save(forEdit);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}