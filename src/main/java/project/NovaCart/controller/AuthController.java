package project.NovaCart.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.NovaCart.dto.LoginRequest;
import project.NovaCart.dto.RegisterRequest;
import project.NovaCart.entity.SystemUser;
import project.NovaCart.service.AuthService;
import project.NovaCart.util.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthService service,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {

        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    
    @PostMapping("/register")
    public SystemUser register(@RequestBody RegisterRequest request) {

        return service.register(request);
    }

   
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( request.getEmail(), request.getPassword()));

        if (authentication.isAuthenticated()) {

            SystemUser dbUser = service.findByEmail(request.getEmail());

            return jwtService.generateToken(dbUser);
        }

        return "Invalid Email or Password";
    }
   
}