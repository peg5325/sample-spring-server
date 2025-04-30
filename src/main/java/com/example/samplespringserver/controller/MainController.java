package com.example.samplespringserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "forward:/health.html";
    }
}
