package com.mgs.aan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserLocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLocation.class);
        UserLocation userLocation1 = new UserLocation();
        userLocation1.setId(1L);
        UserLocation userLocation2 = new UserLocation();
        userLocation2.setId(userLocation1.getId());
        assertThat(userLocation1).isEqualTo(userLocation2);
        userLocation2.setId(2L);
        assertThat(userLocation1).isNotEqualTo(userLocation2);
        userLocation1.setId(null);
        assertThat(userLocation1).isNotEqualTo(userLocation2);
    }
}
