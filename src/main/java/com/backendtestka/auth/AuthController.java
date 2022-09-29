package com.backendtestka.auth;

import com.backendtestka.auth.config.CustomAuthenticationManager;
import com.backendtestka.auth.config.JwtRequest;
import com.backendtestka.auth.config.JwtResponse;
import com.backendtestka.auth.config.JwtTokenUtil;
import com.backendtestka.helpers.Confirmation;
import com.backendtestka.helpers.PasswordEmptyException;
import com.backendtestka.helpers.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private CustomAuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/newuser")
    public ResponseEntity<Confirmation<String>> createNewUser(@RequestBody AccountModel newAccountDetails) {
        // only non-admin accounts can be created with this endpoint
        newAccountDetails.setAdmin(false);
        try {
            authService.createNewUser(newAccountDetails);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(Confirmation.failure("Username already exists.", null),
                                        HttpStatus.BAD_REQUEST);
        } catch (PasswordEmptyException e) {
            return new ResponseEntity<>(Confirmation.failure("Password cannot be an empty string.", null),
                                        HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(Confirmation.successful("New account created", newAccountDetails.getUsername()));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (Exception e) {
            return new ResponseEntity<>("Wrong username and/or password", HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = authService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);
        final JwtResponse jwtResponse = new JwtResponse(token);

        return ResponseEntity.ok(jwtResponse);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

//    @PutMapping("/changepassword")
//    changePassword(){
//        // admins can optionally provide a userId to change passwords for an account that is not theirs
//        // non-admins cannot provide a userId, because they should only be changing passwords for their own account
//        (encoded within JWT)
//        // in either situation, request body has to include the password we'll be changing to
//    }
}
