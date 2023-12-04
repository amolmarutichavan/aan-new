package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostImageMapperTest {

    private PostImageMapper postImageMapper;

    @BeforeEach
    public void setUp() {
        postImageMapper = new PostImageMapperImpl();
    }
}
