package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Country;
import com.mgs.aan.domain.State;
import com.mgs.aan.service.dto.CountryDTO;
import com.mgs.aan.service.dto.StateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link State} and its DTO {@link StateDTO}.
 */
@Mapper(componentModel = "spring")
public interface StateMapper extends EntityMapper<StateDTO, State> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    StateDTO toDto(State s);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);
}
