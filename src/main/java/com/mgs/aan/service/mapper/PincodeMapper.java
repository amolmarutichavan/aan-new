package com.mgs.aan.service.mapper;

import com.mgs.aan.domain.Pincode;
import com.mgs.aan.service.dto.PincodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pincode} and its DTO {@link PincodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PincodeMapper extends EntityMapper<PincodeDTO, Pincode> {}
