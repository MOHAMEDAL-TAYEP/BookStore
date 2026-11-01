package com.AL_Tayep.BookStore.Services;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemCreateDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemReponseDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemSearchDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemSummaryDto;
import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemUpdateDto;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.ItemRepository;
import com.AL_Tayep.BookStore.models.Item;



@Service 
@Transactional(readOnly = true)
public class ItemService {
    
    private final ItemRepository itemRepository;
    public ItemService(ItemRepository itemRepository)
    {
        this.itemRepository=itemRepository;
    }


    // convert item to item reponse dto
    public ItemReponseDto toItemResponseDto(Item item)
    {
        ItemReponseDto dto=new ItemReponseDto(item.getId(),item.getTitle(),item.getDescription(),item.getPrice(),item.getDiscount(),item.getFinalPrice(),item.getAmount());
        return  dto;
    }


      // convert itemCreateDto to item 
    public Item ItemCreateDtoToItem(ItemCreateDto dto)
    {
        Item item=new Item();
        item.setTitle(dto.title());
        item.setDescription(dto.description());
        item.setPrice(dto.price());
        item.setDiscount(dto.discount());
        item.setAmount(dto.amount());
        return  item;
    }

    // convert item to itemSummaryDto
    public ItemSummaryDto toItemSummaryDto(Item item)
    {
         boolean instock=(item.getAmount()>0);
        return  new ItemSummaryDto(item.getId(),item.getTitle(),item.getPrice(),item.getDiscount(),item.getFinalPrice(),instock);
    }

    //  get all itemSummary (List of all item)  
    public List<ItemSummaryDto> getAllItem()
    {
        return itemRepository.findAll().stream().map(this::toItemSummaryDto).toList();
    }

    // get item  info for normal user by using id
    public ItemReponseDto getItem(Long id)
    {
        return   toItemResponseDto(itemRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("there is no item with this id: "+id)));
    }


// delete item by id
@Transactional
  public  void deleteItem(Long id)
    {
        Item user = itemRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("item not found: " + id));
        itemRepository.deleteById(id);
    }

//get item in search by fiter
public List<ItemSummaryDto> getFilteredItems(ItemSearchDto dto)
{
     Specification<Item> spec = ItemSpecificationBuilder.buildSpecification(dto);
    return itemRepository.findAll(spec).stream().map(this::toItemSummaryDto).toList();   
}



//add new item to database
@Transactional
public ItemReponseDto addItem(ItemCreateDto itemCreateDto)
{
    Item item=ItemCreateDtoToItem(itemCreateDto);
    Item savedItem=itemRepository.save(item);
    return  toItemResponseDto(savedItem);
}


// update the user
@Transactional 
public ItemReponseDto updateItem(Long itemId, ItemUpdateDto dto) {
        Item existingItem = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        if (dto.title() != null) {
            existingItem.setTitle(dto.title());
        }
        if (dto.description() != null) {
            existingItem.setDescription(dto.description());
        }
        if (dto.price() != null) {
            existingItem.setPrice(dto.price());
        }
        if (dto.Discount() != null) {
            existingItem.setDiscount(dto.Discount());
        }
        if (dto.amount() != null) {
            existingItem.setAmount(dto.amount().intValue()); 
        }
        existingItem.calculateFinalPrice();

        
        Item savedItem = itemRepository.save(existingItem);

        return toItemResponseDto(savedItem);
    }

}