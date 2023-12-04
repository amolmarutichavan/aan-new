package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VillageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VillageDTO.class);
        VillageDTO villageDTO1 = new VillageDTO();
        villageDTO1.setId(1L);
        VillageDTO villageDTO2 = new VillageDTO();
        assertThat(villageDTO1).isNotEqualTo(villageDTO2);
        villageDTO2.setId(villageDTO1.getId());
        assertThat(villageDTO1).isEqualTo(villageDTO2);
        villageDTO2.setId(2L);
        assertThat(villageDTO1).isNotEqualTo(villageDTO2);
        villageDTO1.setId(null);
        assertThat(villageDTO1).isNotEqualTo(villageDTO2);
    }
}
