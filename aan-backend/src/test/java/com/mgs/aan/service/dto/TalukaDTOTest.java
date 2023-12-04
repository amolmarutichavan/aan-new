package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TalukaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TalukaDTO.class);
        TalukaDTO talukaDTO1 = new TalukaDTO();
        talukaDTO1.setId(1L);
        TalukaDTO talukaDTO2 = new TalukaDTO();
        assertThat(talukaDTO1).isNotEqualTo(talukaDTO2);
        talukaDTO2.setId(talukaDTO1.getId());
        assertThat(talukaDTO1).isEqualTo(talukaDTO2);
        talukaDTO2.setId(2L);
        assertThat(talukaDTO1).isNotEqualTo(talukaDTO2);
        talukaDTO1.setId(null);
        assertThat(talukaDTO1).isNotEqualTo(talukaDTO2);
    }
}
