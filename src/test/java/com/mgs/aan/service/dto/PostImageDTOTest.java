package com.mgs.aan.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostImageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostImageDTO.class);
        PostImageDTO postImageDTO1 = new PostImageDTO();
        postImageDTO1.setId(1L);
        PostImageDTO postImageDTO2 = new PostImageDTO();
        assertThat(postImageDTO1).isNotEqualTo(postImageDTO2);
        postImageDTO2.setId(postImageDTO1.getId());
        assertThat(postImageDTO1).isEqualTo(postImageDTO2);
        postImageDTO2.setId(2L);
        assertThat(postImageDTO1).isNotEqualTo(postImageDTO2);
        postImageDTO1.setId(null);
        assertThat(postImageDTO1).isNotEqualTo(postImageDTO2);
    }
}
