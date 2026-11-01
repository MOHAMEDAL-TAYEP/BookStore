package com.AL_Tayep.BookStore.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.AL_Tayep.BookStore.models.Item;

import jakarta.persistence.LockModeType;

public interface ItemRepository extends JpaRepository<Item,Long>,JpaSpecificationExecutor<Item> {
    public Optional<Item> findByTitle(String title);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);
}
