package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserLocationMapperTest {

    private UserLocationMapper userLocationMapper;

    @BeforeEach
    public void setUp() {
        userLocationMapper = new UserLocationMapperImpl();
    }
}
