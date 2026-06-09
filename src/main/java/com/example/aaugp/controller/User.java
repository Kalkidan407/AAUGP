package com.example.aaugp.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class User{




    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    public String test() {
        return "hello";
    }



}