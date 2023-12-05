package com.mgs.aan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mgs.aan.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostImage.class);
        PostImage postImage1 = new PostImage();
        postImage1.setId(1L);
        PostImage postImage2 = new PostImage();
        postImage2.setId(postImage1.getId());
        assertThat(postImage1).isEqualTo(postImage2);
        postImage2.setId(2L);
        assertThat(postImage1).isNotEqualTo(postImage2);
        postImage1.setId(null);
        assertThat(postImage1).isNotEqualTo(postImage2);
    }
}
