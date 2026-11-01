package com.AL_Tayep.BookStore.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


import com.AL_Tayep.BookStore.Dto.FavDtos.FavItemResponseDto;
import com.AL_Tayep.BookStore.Exceptions.ConflictException;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.FavItemRepository;
import com.AL_Tayep.BookStore.Repositories.ItemRepository;
import com.AL_Tayep.BookStore.Repositories.UserRepository;
import com.AL_Tayep.BookStore.models.FavItem;
import com.AL_Tayep.BookStore.models.Item;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.KeyClass.FavItemId;


@Service
@Transactional(readOnly = true)
public class FavItemService {

    private final FavItemRepository favItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public FavItemService(FavItemRepository favItemRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.favItemRepository = favItemRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private FavItemResponseDto toDto(FavItem f) {
        Item i = f.getItem();
        return new FavItemResponseDto(i.getId(), i.getTitle(), i.getPrice(),
                i.getFinalPrice(), i.getAmount() > 0, f.getCreatedAt());
    }

    public List<FavItemResponseDto> getMyFavorites(User user) {
        return favItemRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public FavItemResponseDto addFavorite(User user, Long itemId) {
        FavItemId id = new FavItemId(user.getId(), itemId);
        if (favItemRepository.existsById(id))
            throw new ConflictException("Item is already in favorites");

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemId));

        FavItem fav = new FavItem();
        fav.setId(id);
        fav.setUser(user);
        fav.setItem(item);
        return toDto(favItemRepository.save(fav));
    }

    @Transactional
    public void removeFavorite(User user, Long itemId) {
        FavItem fav = favItemRepository.findById(new FavItemId(user.getId(), itemId))
            .orElseThrow(() -> new ConflictException("Item is not in favorites"));
        favItemRepository.delete(fav);
    }

    public boolean isFavorite(User user, Long itemId) {
        return favItemRepository.existsById(new FavItemId(user.getId(), itemId));
    }
}