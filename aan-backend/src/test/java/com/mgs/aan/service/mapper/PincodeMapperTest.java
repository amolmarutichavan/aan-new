package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PincodeMapperTest {

    private PincodeMapper pincodeMapper;

    @BeforeEach
    public void setUp() {
        pincodeMapper = new PincodeMapperImpl();
    }
}
