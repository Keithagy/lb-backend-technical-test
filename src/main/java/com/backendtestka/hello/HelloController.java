package com.backendtestka.hello;

import com.backendtestka.auth.config.JwtTokenUtil;
import com.backendtestka.helpers.InvalidAccountIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @GetMapping("/")
    public ResponseEntity<String> sayHello(@RequestHeader(name = "Authorization", required = false) String token) {
        String responseString = "Another Backend Monkey";

        if (token != null && !token.isEmpty()) {
            System.out.println("token");
            System.out.println(token);

            try {
                responseString += ": " + jwtTokenUtil.getAccountFromToken(token).getUsername() + " (" + (jwtTokenUtil.getAdminStatusFromToken(token) ? "Admin)" : "Non" + "-Admin)");

            } catch (InvalidAccountIdException e) {
                return new ResponseEntity<>("Faulty Token Backend Monkey", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return ResponseEntity.ok(responseString);
    }
}
