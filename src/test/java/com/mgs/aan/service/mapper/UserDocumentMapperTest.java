package com.mgs.aan.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDocumentMapperTest {

    private UserDocumentMapper userDocumentMapper;

    @BeforeEach
    public void setUp() {
        userDocumentMapper = new UserDocumentMapperImpl();
    }
}
