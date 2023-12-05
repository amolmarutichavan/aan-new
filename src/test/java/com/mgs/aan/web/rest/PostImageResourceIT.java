package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Post;
import com.mgs.aan.domain.PostImage;
import com.mgs.aan.repository.PostImageRepository;
import com.mgs.aan.service.criteria.PostImageCriteria;
import com.mgs.aan.service.dto.PostImageDTO;
import com.mgs.aan.service.mapper.PostImageMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostImageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostImageResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/post-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostImageMapper postImageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostImageMockMvc;

    private PostImage postImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostImage createEntity(EntityManager em) {
        PostImage postImage = new PostImage()
            .imageUrl(DEFAULT_IMAGE_URL)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .deleted(DEFAULT_DELETED);
        return postImage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostImage createUpdatedEntity(EntityManager em) {
        PostImage postImage = new PostImage()
            .imageUrl(UPDATED_IMAGE_URL)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deleted(UPDATED_DELETED);
        return postImage;
    }

    @BeforeEach
    public void initTest() {
        postImage = createEntity(em);
    }

    @Test
    @Transactional
    void createPostImage() throws Exception {
        int databaseSizeBeforeCreate = postImageRepository.findAll().size();
        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);
        restPostImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postImageDTO)))
            .andExpect(status().isCreated());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeCreate + 1);
        PostImage testPostImage = postImageList.get(postImageList.size() - 1);
        assertThat(testPostImage.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testPostImage.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPostImage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPostImage.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testPostImage.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPostImage.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createPostImageWithExistingId() throws Exception {
        // Create the PostImage with an existing ID
        postImage.setId(1L);
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        int databaseSizeBeforeCreate = postImageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = postImageRepository.findAll().size();
        // set the field null
        postImage.setDeleted(null);

        // Create the PostImage, which fails.
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        restPostImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postImageDTO)))
            .andExpect(status().isBadRequest());

        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPostImages() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList
        restPostImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getPostImage() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get the postImage
        restPostImageMockMvc
            .perform(get(ENTITY_API_URL_ID, postImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(postImage.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getPostImagesByIdFiltering() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        Long id = postImage.getId();

        defaultPostImageShouldBeFound("id.equals=" + id);
        defaultPostImageShouldNotBeFound("id.notEquals=" + id);

        defaultPostImageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPostImageShouldNotBeFound("id.greaterThan=" + id);

        defaultPostImageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPostImageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostImagesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where imageUrl equals to DEFAULT_IMAGE_URL
        defaultPostImageShouldBeFound("imageUrl.equals=" + DEFAULT_IMAGE_URL);

        // Get all the postImageList where imageUrl equals to UPDATED_IMAGE_URL
        defaultPostImageShouldNotBeFound("imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPostImagesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where imageUrl in DEFAULT_IMAGE_URL or UPDATED_IMAGE_URL
        defaultPostImageShouldBeFound("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL);

        // Get all the postImageList where imageUrl equals to UPDATED_IMAGE_URL
        defaultPostImageShouldNotBeFound("imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPostImagesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where imageUrl is not null
        defaultPostImageShouldBeFound("imageUrl.specified=true");

        // Get all the postImageList where imageUrl is null
        defaultPostImageShouldNotBeFound("imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPostImagesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where imageUrl contains DEFAULT_IMAGE_URL
        defaultPostImageShouldBeFound("imageUrl.contains=" + DEFAULT_IMAGE_URL);

        // Get all the postImageList where imageUrl contains UPDATED_IMAGE_URL
        defaultPostImageShouldNotBeFound("imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPostImagesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where imageUrl does not contain DEFAULT_IMAGE_URL
        defaultPostImageShouldNotBeFound("imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);

        // Get all the postImageList where imageUrl does not contain UPDATED_IMAGE_URL
        defaultPostImageShouldBeFound("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdAt equals to DEFAULT_CREATED_AT
        defaultPostImageShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the postImageList where createdAt equals to UPDATED_CREATED_AT
        defaultPostImageShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultPostImageShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the postImageList where createdAt equals to UPDATED_CREATED_AT
        defaultPostImageShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdAt is not null
        defaultPostImageShouldBeFound("createdAt.specified=true");

        // Get all the postImageList where createdAt is null
        defaultPostImageShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdBy equals to DEFAULT_CREATED_BY
        defaultPostImageShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the postImageList where createdBy equals to UPDATED_CREATED_BY
        defaultPostImageShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultPostImageShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the postImageList where createdBy equals to UPDATED_CREATED_BY
        defaultPostImageShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdBy is not null
        defaultPostImageShouldBeFound("createdBy.specified=true");

        // Get all the postImageList where createdBy is null
        defaultPostImageShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdBy contains DEFAULT_CREATED_BY
        defaultPostImageShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the postImageList where createdBy contains UPDATED_CREATED_BY
        defaultPostImageShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where createdBy does not contain DEFAULT_CREATED_BY
        defaultPostImageShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the postImageList where createdBy does not contain UPDATED_CREATED_BY
        defaultPostImageShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultPostImageShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the postImageList where updatedAt equals to UPDATED_UPDATED_AT
        defaultPostImageShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultPostImageShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the postImageList where updatedAt equals to UPDATED_UPDATED_AT
        defaultPostImageShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedAt is not null
        defaultPostImageShouldBeFound("updatedAt.specified=true");

        // Get all the postImageList where updatedAt is null
        defaultPostImageShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultPostImageShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the postImageList where updatedBy equals to UPDATED_UPDATED_BY
        defaultPostImageShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultPostImageShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the postImageList where updatedBy equals to UPDATED_UPDATED_BY
        defaultPostImageShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedBy is not null
        defaultPostImageShouldBeFound("updatedBy.specified=true");

        // Get all the postImageList where updatedBy is null
        defaultPostImageShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedBy contains DEFAULT_UPDATED_BY
        defaultPostImageShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the postImageList where updatedBy contains UPDATED_UPDATED_BY
        defaultPostImageShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultPostImageShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the postImageList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultPostImageShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostImagesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where deleted equals to DEFAULT_DELETED
        defaultPostImageShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the postImageList where deleted equals to UPDATED_DELETED
        defaultPostImageShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllPostImagesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultPostImageShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the postImageList where deleted equals to UPDATED_DELETED
        defaultPostImageShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllPostImagesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        // Get all the postImageList where deleted is not null
        defaultPostImageShouldBeFound("deleted.specified=true");

        // Get all the postImageList where deleted is null
        defaultPostImageShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllPostImagesByPostIsEqualToSomething() throws Exception {
        Post post;
        if (TestUtil.findAll(em, Post.class).isEmpty()) {
            postImageRepository.saveAndFlush(postImage);
            post = PostResourceIT.createEntity(em);
        } else {
            post = TestUtil.findAll(em, Post.class).get(0);
        }
        em.persist(post);
        em.flush();
        postImage.setPost(post);
        postImageRepository.saveAndFlush(postImage);
        Long postId = post.getId();
        // Get all the postImageList where post equals to postId
        defaultPostImageShouldBeFound("postId.equals=" + postId);

        // Get all the postImageList where post equals to (postId + 1)
        defaultPostImageShouldNotBeFound("postId.equals=" + (postId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostImageShouldBeFound(String filter) throws Exception {
        restPostImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(postImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restPostImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostImageShouldNotBeFound(String filter) throws Exception {
        restPostImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPostImage() throws Exception {
        // Get the postImage
        restPostImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPostImage() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();

        // Update the postImage
        PostImage updatedPostImage = postImageRepository.findById(postImage.getId()).get();
        // Disconnect from session so that the updates on updatedPostImage are not directly saved in db
        em.detach(updatedPostImage);
        updatedPostImage
            .imageUrl(UPDATED_IMAGE_URL)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deleted(UPDATED_DELETED);
        PostImageDTO postImageDTO = postImageMapper.toDto(updatedPostImage);

        restPostImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
        PostImage testPostImage = postImageList.get(postImageList.size() - 1);
        assertThat(testPostImage.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testPostImage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPostImage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPostImage.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPostImage.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPostImage.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingPostImage() throws Exception {
        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();
        postImage.setId(count.incrementAndGet());

        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPostImage() throws Exception {
        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();
        postImage.setId(count.incrementAndGet());

        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPostImage() throws Exception {
        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();
        postImage.setId(count.incrementAndGet());

        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostImageWithPatch() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();

        // Update the postImage using partial update
        PostImage partialUpdatedPostImage = new PostImage();
        partialUpdatedPostImage.setId(postImage.getId());

        partialUpdatedPostImage.createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY).deleted(UPDATED_DELETED);

        restPostImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPostImage))
            )
            .andExpect(status().isOk());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
        PostImage testPostImage = postImageList.get(postImageList.size() - 1);
        assertThat(testPostImage.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testPostImage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPostImage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPostImage.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testPostImage.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPostImage.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdatePostImageWithPatch() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();

        // Update the postImage using partial update
        PostImage partialUpdatedPostImage = new PostImage();
        partialUpdatedPostImage.setId(postImage.getId());

        partialUpdatedPostImage
            .imageUrl(UPDATED_IMAGE_URL)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deleted(UPDATED_DELETED);

        restPostImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPostImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPostImage))
            )
            .andExpect(status().isOk());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
        PostImage testPostImage = postImageList.get(postImageList.size() - 1);
        assertThat(testPostImage.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testPostImage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPostImage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPostImage.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPostImage.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPostImage.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingPostImage() throws Exception {
        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();
        postImage.setId(count.incrementAndGet());

        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPostImage() throws Exception {
        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();
        postImage.setId(count.incrementAndGet());

        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPostImage() throws Exception {
        int databaseSizeBeforeUpdate = postImageRepository.findAll().size();
        postImage.setId(count.incrementAndGet());

        // Create the PostImage
        PostImageDTO postImageDTO = postImageMapper.toDto(postImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostImageMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(postImageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PostImage in the database
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePostImage() throws Exception {
        // Initialize the database
        postImageRepository.saveAndFlush(postImage);

        int databaseSizeBeforeDelete = postImageRepository.findAll().size();

        // Delete the postImage
        restPostImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, postImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PostImage> postImageList = postImageRepository.findAll();
        assertThat(postImageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
