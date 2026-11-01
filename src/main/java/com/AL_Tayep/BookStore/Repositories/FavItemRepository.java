package com.AL_Tayep.BookStore.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AL_Tayep.BookStore.models.FavItem;
import com.AL_Tayep.BookStore.models.KeyClass.FavItemId;

public interface FavItemRepository extends JpaRepository<FavItem,FavItemId>{
    
     List<FavItem> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
