package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VarietyMapperTest {

    private VarietyMapper varietyMapper;

    @BeforeEach
    public void setUp() {
        varietyMapper = new VarietyMapperImpl();
    }
}
