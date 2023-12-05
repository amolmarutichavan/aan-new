package com.mgs.aan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VarietyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Variety.class);
        Variety variety1 = new Variety();
        variety1.setId(1L);
        Variety variety2 = new Variety();
        variety2.setId(variety1.getId());
        assertThat(variety1).isEqualTo(variety2);
        variety2.setId(2L);
        assertThat(variety1).isNotEqualTo(variety2);
        variety1.setId(null);
        assertThat(variety1).isNotEqualTo(variety2);
    }
}
