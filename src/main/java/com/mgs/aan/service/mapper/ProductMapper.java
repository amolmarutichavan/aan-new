package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Category;
import com.mgs.aan.domain.Product;
import com.mgs.aan.domain.User;
import com.mgs.aan.service.dto.CategoryDTO;
import com.mgs.aan.service.dto.ProductDTO;
import com.mgs.aan.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    @Mapping(target = "users", source = "users", qualifiedByName = "userIdSet")
    ProductDTO toDto(Product s);

    @Mapping(target = "removeUser", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }
}
