package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserDocumentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserDocumentDTO.class);
        UserDocumentDTO userDocumentDTO1 = new UserDocumentDTO();
        userDocumentDTO1.setId(1L);
        UserDocumentDTO userDocumentDTO2 = new UserDocumentDTO();
        assertThat(userDocumentDTO1).isNotEqualTo(userDocumentDTO2);
        userDocumentDTO2.setId(userDocumentDTO1.getId());
        assertThat(userDocumentDTO1).isEqualTo(userDocumentDTO2);
        userDocumentDTO2.setId(2L);
        assertThat(userDocumentDTO1).isNotEqualTo(userDocumentDTO2);
        userDocumentDTO1.setId(null);
        assertThat(userDocumentDTO1).isNotEqualTo(userDocumentDTO2);
    }
}
