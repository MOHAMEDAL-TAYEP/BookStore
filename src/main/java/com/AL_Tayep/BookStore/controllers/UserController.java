package com.AL_Tayep.BookStore.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.UserDtos.ChangePasswordDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.UserCreateDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.UserResponseDto;
import com.AL_Tayep.BookStore.Services.UserService;
import com.AL_Tayep.BookStore.models.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<UserResponseDto> updateUser(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserCreateDto dto) {
                 User user=(User) userDetails;
        return ResponseEntity.ok(userService.updateUser(dto, user));
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails
           , @Valid @RequestBody ChangePasswordDto dto) {
        User user=(User) userDetails;
        userService.updatePassword(dto, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
