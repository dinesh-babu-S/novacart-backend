package project.NovaCart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import project.NovaCart.jwt.JwtUtil;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/generate")
    public String generateToken(@RequestParam String username,
                                @RequestParam String email) {

        return jwtUtil.generateToken(username, email);
    }
}