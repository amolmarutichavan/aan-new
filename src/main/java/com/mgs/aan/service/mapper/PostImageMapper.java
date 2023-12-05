package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Post;
import com.mgs.aan.domain.PostImage;
import com.mgs.aan.service.dto.PostDTO;
import com.mgs.aan.service.dto.PostImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PostImage} and its DTO {@link PostImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostImageMapper extends EntityMapper<PostImageDTO, PostImage> {
    @Mapping(target = "post", source = "post", qualifiedByName = "postId")
    PostImageDTO toDto(PostImage s);

    @Named("postId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PostDTO toDtoPostId(Post post);
}
