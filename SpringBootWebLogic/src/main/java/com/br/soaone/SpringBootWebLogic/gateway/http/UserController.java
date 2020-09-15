package com.br.soaone.SpringBootWebLogic.gateway.http;

import com.br.soaone.SpringBootWebLogic.gateway.json.User;
import com.br.soaone.SpringBootWebLogic.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<String> create(@RequestBody User user) throws ExecutionException, InterruptedException, JsonProcessingException {
        return userService.execute(user);
        
    }

    @PostMapping("/user")
    public User newuser(@RequestBody User user) throws ExecutionException, InterruptedException, JsonProcessingException {
    	System.out.println("(Producer) NOME: " + user.getName());
        return user;
        
    }
    
}