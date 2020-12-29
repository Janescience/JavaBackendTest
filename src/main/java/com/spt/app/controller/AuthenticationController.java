package com.spt.app.controller;

import com.spt.app.model.AuthenticationRequest;
import com.spt.app.respository.UserRepository;
import com.spt.app.security.JWTPrinciple;
import com.spt.app.service.UserService;
import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTPrinciple JWTPrinciple;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = JWTPrinciple.generateToken(userDetails);

        Map userInfoMap = new HashMap();
        userInfoMap.put("token", token);
        userInfoMap.put("userInfo", userRepository.findByUsername(authenticationRequest.getUsername()));

        Map dataMap = new HashMap();
        dataMap.put("data", userInfoMap);
        dataMap.put("message", "Authenticated");
        dataMap.put("code", "200");

        Map resultMap = new HashMap();
        resultMap.put("success", dataMap);

        return new JSONSerializer()
                .prettyPrint(true)
                .exclude("*.class")
                .exclude("*.password")
                .deepSerialize(resultMap);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveUser(@RequestBody String user) throws Exception {
        return new JSONSerializer()
                .prettyPrint(true)
                .exclude("*.class")
                .exclude("*.password")
                .deepSerialize(userService.save(user));
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
}