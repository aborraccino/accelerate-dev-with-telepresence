package com.exampleservice.telepresence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleServiceTelepresenceController {


    @Autowired
    BuildProperties buildProperties;

    @GetMapping
    public String home(){
        return "Hello from " + buildProperties.getName() + " "  + buildProperties.getVersion() + "!";
    }

}
