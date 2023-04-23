package ru.veselov.websocketroomproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/api/auth")
    public String index() {
        return "test";
    }
}