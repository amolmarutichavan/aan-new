package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserReviewMapperTest {

    private UserReviewMapper userReviewMapper;

    @BeforeEach
    public void setUp() {
        userReviewMapper = new UserReviewMapperImpl();
    }
}
