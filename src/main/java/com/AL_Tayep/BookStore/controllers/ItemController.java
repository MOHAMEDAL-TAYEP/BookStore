package com.AL_Tayep.BookStore.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemCreateDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemReponseDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemSearchDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemSummaryDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemUpdateDto;
import com.AL_Tayep.BookStore.Services.ItemService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService)
    {
        this.itemService=itemService;
    }

     @GetMapping
     public ResponseEntity<List<ItemSummaryDto>> getAllItems() {
         return ResponseEntity.ok(itemService.getAllItem());
     }
     
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemReponseDto> getItme(@PathVariable Long id)
    {
        return ResponseEntity.ok(itemService.getItem(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id)
    {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/search")
    public ResponseEntity<List<ItemSummaryDto>> searchForItems(@RequestBody ItemSearchDto dto) {
        return ResponseEntity.ok(itemService.getFilteredItems(dto));
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ItemReponseDto> addNewItem(@RequestBody ItemCreateDto dto) {        
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addItem(dto));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ItemReponseDto> updateItem(@PathVariable Long id, @RequestBody ItemUpdateDto dto) {
        return ResponseEntity.ok( itemService.updateItem(id, dto));
    }
    
}
