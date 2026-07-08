package project.NovaCart.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import project.NovaCart.entity.SystemUser;
import project.NovaCart.exception.ResourceNotFoundException;
import project.NovaCart.repository.UserRepo;

@Service
public class SecurityService {

    private final UserRepo userRepo;

    public SecurityService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // Get Logged-in User
    public SystemUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }
}