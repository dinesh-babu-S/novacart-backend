package project.NovaCart.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import project.NovaCart.dto.RegisterRequest;
import project.NovaCart.entity.SystemUser;
import project.NovaCart.exception.BadRequestException;
import project.NovaCart.repository.UserRepo;

@Service
public class AuthService {

    private final UserRepo repo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepo repo,
                       PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // Register User
    public SystemUser register(RegisterRequest request) {

        if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists.");
        }

        SystemUser user = new SystemUser();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return repo.save(user);
    }

    // Find User
    public SystemUser findByEmail(String email) {

        return repo.findByEmail(email).orElseThrow(() ->new BadRequestException("User not found."));
    }

}