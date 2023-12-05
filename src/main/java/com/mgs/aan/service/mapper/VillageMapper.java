package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Taluka;
import com.mgs.aan.domain.Village;
import com.mgs.aan.service.dto.TalukaDTO;
import com.mgs.aan.service.dto.VillageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Village} and its DTO {@link VillageDTO}.
 */
@Mapper(componentModel = "spring")
public interface VillageMapper extends EntityMapper<VillageDTO, Village> {
    @Mapping(target = "taluka", source = "taluka", qualifiedByName = "talukaId")
    VillageDTO toDto(Village s);

    @Named("talukaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TalukaDTO toDtoTalukaId(Taluka taluka);
}
