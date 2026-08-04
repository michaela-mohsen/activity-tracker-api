package com.mm.activitytracker.controller;

import com.mm.user.core.entity.User;
import com.mm.user.core.exception.TokenRefreshException;
import com.mm.user.core.exception.UserAlreadyExistsException;
import com.mm.user.core.model.*;
import com.mm.user.core.service.AuthenticationService;
import com.mm.user.core.service.RefreshTokenService;
import com.mm.user.core.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody AuthRequest authRequest, BindingResult bindingResult) {
       try {
           if (bindingResult.hasErrors()) {
               log.error("Request info is missing");
               return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
           }
           AuthResponse authResponse = authenticationService.authenticateUser(authRequest);
           return new ResponseEntity<>(authResponse, HttpStatus.OK);
       } catch (BadCredentialsException badCredentialsException) {
            log.error("Invalid credentials provided");
            return new ResponseEntity<>("Invalid credentials", HttpStatus.BAD_REQUEST);
       } catch (Exception exception) {
            log.error("An error occurred", exception);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRequest userRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("User request info is missing");
                return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
            }
            User newUser = userService.saveUser(userRequest);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } catch (UserAlreadyExistsException alreadyExistsException) {
            log.error(alreadyExistsException.getMessage());
            return new ResponseEntity<>(alreadyExistsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            log.error("An error occurred", exception);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            RefreshTokenResponse response = refreshTokenService.refreshToken(refreshRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TokenRefreshException te) {
            log.error(te.getMessage());
            return new ResponseEntity<>(te.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sign-out")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> logoutUser() {
        log.info("Begin log out");
        try {
            refreshTokenService.delete();
            return new ResponseEntity<>("User successfully logged out.", HttpStatus.OK);
        } catch (IllegalArgumentException ie) {
            log.error(ie.getMessage());
            return new ResponseEntity<>(ie.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
