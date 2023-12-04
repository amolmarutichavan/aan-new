package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.District;
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.service.dto.DistrictDTO;
import com.mgs.aan.service.dto.TalukaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Taluka} and its DTO {@link TalukaDTO}.
 */
@Mapper(componentModel = "spring")
public interface TalukaMapper extends EntityMapper<TalukaDTO, Taluka> {
    @Mapping(target = "district", source = "district", qualifiedByName = "districtId")
    TalukaDTO toDto(Taluka s);

    @Named("districtId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DistrictDTO toDtoDistrictId(District district);
}
