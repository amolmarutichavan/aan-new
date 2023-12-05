package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Product;
import com.mgs.aan.domain.Variety;
import com.mgs.aan.service.dto.ProductDTO;
import com.mgs.aan.service.dto.VarietyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Variety} and its DTO {@link VarietyDTO}.
 */
@Mapper(componentModel = "spring")
public interface VarietyMapper extends EntityMapper<VarietyDTO, Variety> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    VarietyDTO toDto(Variety s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
