package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserLocationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLocationDTO.class);
        UserLocationDTO userLocationDTO1 = new UserLocationDTO();
        userLocationDTO1.setId(1L);
        UserLocationDTO userLocationDTO2 = new UserLocationDTO();
        assertThat(userLocationDTO1).isNotEqualTo(userLocationDTO2);
        userLocationDTO2.setId(userLocationDTO1.getId());
        assertThat(userLocationDTO1).isEqualTo(userLocationDTO2);
        userLocationDTO2.setId(2L);
        assertThat(userLocationDTO1).isNotEqualTo(userLocationDTO2);
        userLocationDTO1.setId(null);
        assertThat(userLocationDTO1).isNotEqualTo(userLocationDTO2);
    }
}
