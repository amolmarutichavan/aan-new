package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.User;
import com.mgs.aan.domain.UserDocument;
import com.mgs.aan.service.dto.UserDTO;
import com.mgs.aan.service.dto.UserDocumentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserDocument} and its DTO {@link UserDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserDocumentMapper extends EntityMapper<UserDocumentDTO, UserDocument> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    UserDocumentDTO toDto(UserDocument s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
