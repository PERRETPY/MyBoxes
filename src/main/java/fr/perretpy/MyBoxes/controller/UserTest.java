package fr.perretpy.myBoxes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.perretpy.myBoxes.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class UserTest {

    @GetMapping("/")
    @PreAuthorize("hasRole('myboxes_admin')")
    public ResponseEntity<Object> getTest(Authentication authentication) {
        User user;
        try {
            user = User.mapAuthenticationToUser(authentication);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}