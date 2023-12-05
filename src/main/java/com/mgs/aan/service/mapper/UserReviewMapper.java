package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.User;
import com.mgs.aan.domain.UserReview;
import com.mgs.aan.service.dto.UserDTO;
import com.mgs.aan.service.dto.UserReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserReview} and its DTO {@link UserReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserReviewMapper extends EntityMapper<UserReviewDTO, UserReview> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    UserReviewDTO toDto(UserReview s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
