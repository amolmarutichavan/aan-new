package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VillageMapperTest {

    private VillageMapper villageMapper;

    @BeforeEach
    public void setUp() {
        villageMapper = new VillageMapperImpl();
    }
}
