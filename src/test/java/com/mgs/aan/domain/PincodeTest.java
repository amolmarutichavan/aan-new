package com.mgs.aan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PincodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pincode.class);
        Pincode pincode1 = new Pincode();
        pincode1.setId(1L);
        Pincode pincode2 = new Pincode();
        pincode2.setId(pincode1.getId());
        assertThat(pincode1).isEqualTo(pincode2);
        pincode2.setId(2L);
        assertThat(pincode1).isNotEqualTo(pincode2);
        pincode1.setId(null);
        assertThat(pincode1).isNotEqualTo(pincode2);
    }
}
