package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.District;
import com.mgs.aan.domain.State;
import com.mgs.aan.service.dto.DistrictDTO;
import com.mgs.aan.service.dto.StateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link District} and its DTO {@link DistrictDTO}.
 */
@Mapper(componentModel = "spring")
public interface DistrictMapper extends EntityMapper<DistrictDTO, District> {
    @Mapping(target = "state", source = "state", qualifiedByName = "stateId")
    DistrictDTO toDto(District s);

    @Named("stateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StateDTO toDtoStateId(State state);
}
