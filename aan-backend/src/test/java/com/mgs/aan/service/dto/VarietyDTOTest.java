package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VarietyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VarietyDTO.class);
        VarietyDTO varietyDTO1 = new VarietyDTO();
        varietyDTO1.setId(1L);
        VarietyDTO varietyDTO2 = new VarietyDTO();
        assertThat(varietyDTO1).isNotEqualTo(varietyDTO2);
        varietyDTO2.setId(varietyDTO1.getId());
        assertThat(varietyDTO1).isEqualTo(varietyDTO2);
        varietyDTO2.setId(2L);
        assertThat(varietyDTO1).isNotEqualTo(varietyDTO2);
        varietyDTO1.setId(null);
        assertThat(varietyDTO1).isNotEqualTo(varietyDTO2);
    }
}
