package project.NovaCart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.NovaCart.entity.SystemUser;

public interface  UserRepo extends JpaRepository<SystemUser,Long>{
     Optional<SystemUser> findByEmail(String email);

}
