package com.AL_Tayep.BookStore.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AL_Tayep.BookStore.models.User;


public interface UserRepository extends JpaRepository<User,Long> {
    
    public Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
