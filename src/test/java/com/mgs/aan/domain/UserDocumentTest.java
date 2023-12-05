package com.mgs.aan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserDocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserDocument.class);
        UserDocument userDocument1 = new UserDocument();
        userDocument1.setId(1L);
        UserDocument userDocument2 = new UserDocument();
        userDocument2.setId(userDocument1.getId());
        assertThat(userDocument1).isEqualTo(userDocument2);
        userDocument2.setId(2L);
        assertThat(userDocument1).isNotEqualTo(userDocument2);
        userDocument1.setId(null);
        assertThat(userDocument1).isNotEqualTo(userDocument2);
    }
}
