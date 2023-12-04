package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Post;
import com.mgs.aan.domain.Product;
import com.mgs.aan.domain.User;
import com.mgs.aan.service.dto.PostDTO;
import com.mgs.aan.service.dto.ProductDTO;
import com.mgs.aan.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Post} and its DTO {@link PostDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostMapper extends EntityMapper<PostDTO, Post> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productIdSet")
    PostDTO toDto(Post s);

    @Mapping(target = "removeProduct", ignore = true)
    Post toEntity(PostDTO postDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("productIdSet")
    default Set<ProductDTO> toDtoProductIdSet(Set<Product> product) {
        return product.stream().map(this::toDtoProductId).collect(Collectors.toSet());
    }
}
