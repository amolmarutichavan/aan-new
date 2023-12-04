package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Country;
import com.mgs.aan.domain.District;
import com.mgs.aan.domain.Pincode;
import com.mgs.aan.domain.State;
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.domain.User;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.domain.Village;
import com.mgs.aan.service.dto.CountryDTO;
import com.mgs.aan.service.dto.DistrictDTO;
import com.mgs.aan.service.dto.PincodeDTO;
import com.mgs.aan.service.dto.StateDTO;
import com.mgs.aan.service.dto.TalukaDTO;
import com.mgs.aan.service.dto.UserDTO;
import com.mgs.aan.service.dto.UserLocationDTO;
import com.mgs.aan.service.dto.VillageDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserLocation} and its DTO {@link UserLocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserLocationMapper extends EntityMapper<UserLocationDTO, UserLocation> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "pincodes", source = "pincodes", qualifiedByName = "pincodeIdSet")
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    @Mapping(target = "state", source = "state", qualifiedByName = "stateId")
    @Mapping(target = "district", source = "district", qualifiedByName = "districtId")
    @Mapping(target = "taluka", source = "taluka", qualifiedByName = "talukaId")
    @Mapping(target = "village", source = "village", qualifiedByName = "villageId")
    UserLocationDTO toDto(UserLocation s);

    @Mapping(target = "removePincodes", ignore = true)
    UserLocation toEntity(UserLocationDTO userLocationDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("pincodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PincodeDTO toDtoPincodeId(Pincode pincode);

    @Named("pincodeIdSet")
    default Set<PincodeDTO> toDtoPincodeIdSet(Set<Pincode> pincode) {
        return pincode.stream().map(this::toDtoPincodeId).collect(Collectors.toSet());
    }

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);

    @Named("stateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StateDTO toDtoStateId(State state);

    @Named("districtId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DistrictDTO toDtoDistrictId(District district);

    @Named("talukaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TalukaDTO toDtoTalukaId(Taluka taluka);

    @Named("villageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VillageDTO toDtoVillageId(Village village);
}
