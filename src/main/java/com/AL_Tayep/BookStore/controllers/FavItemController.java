package com.AL_Tayep.BookStore.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.FavDtos.FavItemResponseDto;
import com.AL_Tayep.BookStore.Services.FavItemService;
import com.AL_Tayep.BookStore.models.User;

@RestController
@RequestMapping("/api/favorites/users/")
public class FavItemController {

    private final FavItemService favItemService;

    public FavItemController(FavItemService favItemService) {
        this.favItemService = favItemService;
    }

    // list my favorites
    @GetMapping
    public ResponseEntity<List<FavItemResponseDto>> getMyFavorites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(favItemService.getMyFavorites(user));
    }

    // add an item to favorites
    @PostMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FavItemResponseDto> addFavorite(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        User user = (User) userDetails;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favItemService.addFavorite(user, itemId));
    }

    // remove an item from favorites
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeFavorite(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
                 User user=(User) userDetails;
        favItemService.removeFavorite(user, itemId);
        return ResponseEntity.noContent().build();
    }

    // is this item one of my favorites? (true / false)
    @GetMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> isFavorite(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
                 User user=(User) userDetails;
        return ResponseEntity.ok(favItemService.isFavorite(user, itemId));
    }
}