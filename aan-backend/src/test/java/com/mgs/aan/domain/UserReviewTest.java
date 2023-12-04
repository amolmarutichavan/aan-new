package com.mgs.aan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserReview.class);
        UserReview userReview1 = new UserReview();
        userReview1.setId(1L);
        UserReview userReview2 = new UserReview();
        userReview2.setId(userReview1.getId());
        assertThat(userReview1).isEqualTo(userReview2);
        userReview2.setId(2L);
        assertThat(userReview1).isNotEqualTo(userReview2);
        userReview1.setId(null);
        assertThat(userReview1).isNotEqualTo(userReview2);
    }
}
