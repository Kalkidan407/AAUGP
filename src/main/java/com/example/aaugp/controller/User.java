package com.example.aaugp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aaugp.dto.user.UserRequest;
import com.example.aaugp.dto.user.UserResponse;
import com.example.aaugp.services.UserServices;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class User{

      private final UserServices userService;


      @PostMapping("/create")
@ResponseStatus(HttpStatus.CREATED)
public UserResponse createUser(@Valid @RequestBody UserRequest user) {
  return userService.createUser(user);
}

  @GetMapping("/id/{id}")
    public UserResponse  getUserById(@PathVariable Long id) {
        return  userService.getUsersById(id) ;
    } 
 

      @DeleteMapping("/delete/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUserById(@PathVariable Long id){
    userService.deleteUserById(id);
  }



}