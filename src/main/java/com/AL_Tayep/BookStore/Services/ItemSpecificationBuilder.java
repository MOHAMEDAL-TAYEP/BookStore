package com.AL_Tayep.BookStore.Services;

import com.AL_Tayep.BookStore.Dto.ItemDtos.ItemSearchDto;
import com.AL_Tayep.BookStore.models.Item;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ItemSpecificationBuilder {

    public static Specification<Item> buildSpecification(ItemSearchDto criteria) {
        Specification<Item> spec = (root, query, cb) -> cb.conjunction();

        if (criteria == null) {
            return spec;
        }

        
        if (StringUtils.hasText(criteria.keyWord())) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + criteria.keyWord().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + criteria.keyWord().toLowerCase() + "%")
                )
            );
        }

        
        if (criteria.minPrice() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("finalPrice"), criteria.minPrice())
            );
        }

        
        if (criteria.MaxPrice() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("finalPrice"), criteria.MaxPrice())
            );
        }

        return spec;
    }
}