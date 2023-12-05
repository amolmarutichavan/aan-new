package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PincodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PincodeDTO.class);
        PincodeDTO pincodeDTO1 = new PincodeDTO();
        pincodeDTO1.setId(1L);
        PincodeDTO pincodeDTO2 = new PincodeDTO();
        assertThat(pincodeDTO1).isNotEqualTo(pincodeDTO2);
        pincodeDTO2.setId(pincodeDTO1.getId());
        assertThat(pincodeDTO1).isEqualTo(pincodeDTO2);
        pincodeDTO2.setId(2L);
        assertThat(pincodeDTO1).isNotEqualTo(pincodeDTO2);
        pincodeDTO1.setId(null);
        assertThat(pincodeDTO1).isNotEqualTo(pincodeDTO2);
    }
}
