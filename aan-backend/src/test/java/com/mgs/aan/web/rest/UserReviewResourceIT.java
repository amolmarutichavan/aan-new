package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.User;
import com.mgs.aan.domain.UserReview;
import com.mgs.aan.domain.enumeration.Category;
import com.mgs.aan.repository.UserReviewRepository;
import com.mgs.aan.service.criteria.UserReviewCriteria;
import com.mgs.aan.service.dto.UserReviewDTO;
import com.mgs.aan.service.mapper.UserReviewMapper;
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
 * Integration tests for the {@link UserReviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserReviewResourceIT {

    private static final Instant DEFAULT_TIMES_TAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMES_TAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final String DEFAULT_REVIEW = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW = "BBBBBBBBBB";

    private static final Category DEFAULT_CATEGORY = Category.GENERAL;
    private static final Category UPDATED_CATEGORY = Category.PRODUCT;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/user-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserReviewMockMvc;

    private UserReview userReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserReview createEntity(EntityManager em) {
        UserReview userReview = new UserReview()
            .timesTamp(DEFAULT_TIMES_TAMP)
            .rating(DEFAULT_RATING)
            .review(DEFAULT_REVIEW)
            .category(DEFAULT_CATEGORY)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return userReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserReview createUpdatedEntity(EntityManager em) {
        UserReview userReview = new UserReview()
            .timesTamp(UPDATED_TIMES_TAMP)
            .rating(UPDATED_RATING)
            .review(UPDATED_REVIEW)
            .category(UPDATED_CATEGORY)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return userReview;
    }

    @BeforeEach
    public void initTest() {
        userReview = createEntity(em);
    }

    @Test
    @Transactional
    void createUserReview() throws Exception {
        int databaseSizeBeforeCreate = userReviewRepository.findAll().size();
        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);
        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReviewDTO)))
            .andExpect(status().isCreated());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeCreate + 1);
        UserReview testUserReview = userReviewList.get(userReviewList.size() - 1);
        assertThat(testUserReview.getTimesTamp()).isEqualTo(DEFAULT_TIMES_TAMP);
        assertThat(testUserReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testUserReview.getReview()).isEqualTo(DEFAULT_REVIEW);
        assertThat(testUserReview.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testUserReview.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUserReview.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testUserReview.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserReview.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testUserReview.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createUserReviewWithExistingId() throws Exception {
        // Create the UserReview with an existing ID
        userReview.setId(1L);
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        int databaseSizeBeforeCreate = userReviewRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = userReviewRepository.findAll().size();
        // set the field null
        userReview.setActive(null);

        // Create the UserReview, which fails.
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        restUserReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReviewDTO)))
            .andExpect(status().isBadRequest());

        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserReviews() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].timesTamp").value(hasItem(DEFAULT_TIMES_TAMP.toString())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].review").value(hasItem(DEFAULT_REVIEW)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getUserReview() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get the userReview
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, userReview.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userReview.getId().intValue()))
            .andExpect(jsonPath("$.timesTamp").value(DEFAULT_TIMES_TAMP.toString()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.review").value(DEFAULT_REVIEW))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getUserReviewsByIdFiltering() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        Long id = userReview.getId();

        defaultUserReviewShouldBeFound("id.equals=" + id);
        defaultUserReviewShouldNotBeFound("id.notEquals=" + id);

        defaultUserReviewShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserReviewShouldNotBeFound("id.greaterThan=" + id);

        defaultUserReviewShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserReviewShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserReviewsByTimesTampIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where timesTamp equals to DEFAULT_TIMES_TAMP
        defaultUserReviewShouldBeFound("timesTamp.equals=" + DEFAULT_TIMES_TAMP);

        // Get all the userReviewList where timesTamp equals to UPDATED_TIMES_TAMP
        defaultUserReviewShouldNotBeFound("timesTamp.equals=" + UPDATED_TIMES_TAMP);
    }

    @Test
    @Transactional
    void getAllUserReviewsByTimesTampIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where timesTamp in DEFAULT_TIMES_TAMP or UPDATED_TIMES_TAMP
        defaultUserReviewShouldBeFound("timesTamp.in=" + DEFAULT_TIMES_TAMP + "," + UPDATED_TIMES_TAMP);

        // Get all the userReviewList where timesTamp equals to UPDATED_TIMES_TAMP
        defaultUserReviewShouldNotBeFound("timesTamp.in=" + UPDATED_TIMES_TAMP);
    }

    @Test
    @Transactional
    void getAllUserReviewsByTimesTampIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where timesTamp is not null
        defaultUserReviewShouldBeFound("timesTamp.specified=true");

        // Get all the userReviewList where timesTamp is null
        defaultUserReviewShouldNotBeFound("timesTamp.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating equals to DEFAULT_RATING
        defaultUserReviewShouldBeFound("rating.equals=" + DEFAULT_RATING);

        // Get all the userReviewList where rating equals to UPDATED_RATING
        defaultUserReviewShouldNotBeFound("rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating in DEFAULT_RATING or UPDATED_RATING
        defaultUserReviewShouldBeFound("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING);

        // Get all the userReviewList where rating equals to UPDATED_RATING
        defaultUserReviewShouldNotBeFound("rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating is not null
        defaultUserReviewShouldBeFound("rating.specified=true");

        // Get all the userReviewList where rating is null
        defaultUserReviewShouldNotBeFound("rating.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating is greater than or equal to DEFAULT_RATING
        defaultUserReviewShouldBeFound("rating.greaterThanOrEqual=" + DEFAULT_RATING);

        // Get all the userReviewList where rating is greater than or equal to UPDATED_RATING
        defaultUserReviewShouldNotBeFound("rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating is less than or equal to DEFAULT_RATING
        defaultUserReviewShouldBeFound("rating.lessThanOrEqual=" + DEFAULT_RATING);

        // Get all the userReviewList where rating is less than or equal to SMALLER_RATING
        defaultUserReviewShouldNotBeFound("rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating is less than DEFAULT_RATING
        defaultUserReviewShouldNotBeFound("rating.lessThan=" + DEFAULT_RATING);

        // Get all the userReviewList where rating is less than UPDATED_RATING
        defaultUserReviewShouldBeFound("rating.lessThan=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllUserReviewsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where rating is greater than DEFAULT_RATING
        defaultUserReviewShouldNotBeFound("rating.greaterThan=" + DEFAULT_RATING);

        // Get all the userReviewList where rating is greater than SMALLER_RATING
        defaultUserReviewShouldBeFound("rating.greaterThan=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllUserReviewsByReviewIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where review equals to DEFAULT_REVIEW
        defaultUserReviewShouldBeFound("review.equals=" + DEFAULT_REVIEW);

        // Get all the userReviewList where review equals to UPDATED_REVIEW
        defaultUserReviewShouldNotBeFound("review.equals=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllUserReviewsByReviewIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where review in DEFAULT_REVIEW or UPDATED_REVIEW
        defaultUserReviewShouldBeFound("review.in=" + DEFAULT_REVIEW + "," + UPDATED_REVIEW);

        // Get all the userReviewList where review equals to UPDATED_REVIEW
        defaultUserReviewShouldNotBeFound("review.in=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllUserReviewsByReviewIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where review is not null
        defaultUserReviewShouldBeFound("review.specified=true");

        // Get all the userReviewList where review is null
        defaultUserReviewShouldNotBeFound("review.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByReviewContainsSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where review contains DEFAULT_REVIEW
        defaultUserReviewShouldBeFound("review.contains=" + DEFAULT_REVIEW);

        // Get all the userReviewList where review contains UPDATED_REVIEW
        defaultUserReviewShouldNotBeFound("review.contains=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllUserReviewsByReviewNotContainsSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where review does not contain DEFAULT_REVIEW
        defaultUserReviewShouldNotBeFound("review.doesNotContain=" + DEFAULT_REVIEW);

        // Get all the userReviewList where review does not contain UPDATED_REVIEW
        defaultUserReviewShouldBeFound("review.doesNotContain=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where category equals to DEFAULT_CATEGORY
        defaultUserReviewShouldBeFound("category.equals=" + DEFAULT_CATEGORY);

        // Get all the userReviewList where category equals to UPDATED_CATEGORY
        defaultUserReviewShouldNotBeFound("category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where category in DEFAULT_CATEGORY or UPDATED_CATEGORY
        defaultUserReviewShouldBeFound("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY);

        // Get all the userReviewList where category equals to UPDATED_CATEGORY
        defaultUserReviewShouldNotBeFound("category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where category is not null
        defaultUserReviewShouldBeFound("category.specified=true");

        // Get all the userReviewList where category is null
        defaultUserReviewShouldNotBeFound("category.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdAt equals to DEFAULT_CREATED_AT
        defaultUserReviewShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the userReviewList where createdAt equals to UPDATED_CREATED_AT
        defaultUserReviewShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultUserReviewShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the userReviewList where createdAt equals to UPDATED_CREATED_AT
        defaultUserReviewShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdAt is not null
        defaultUserReviewShouldBeFound("createdAt.specified=true");

        // Get all the userReviewList where createdAt is null
        defaultUserReviewShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdBy equals to DEFAULT_CREATED_BY
        defaultUserReviewShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the userReviewList where createdBy equals to UPDATED_CREATED_BY
        defaultUserReviewShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultUserReviewShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the userReviewList where createdBy equals to UPDATED_CREATED_BY
        defaultUserReviewShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdBy is not null
        defaultUserReviewShouldBeFound("createdBy.specified=true");

        // Get all the userReviewList where createdBy is null
        defaultUserReviewShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdBy contains DEFAULT_CREATED_BY
        defaultUserReviewShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the userReviewList where createdBy contains UPDATED_CREATED_BY
        defaultUserReviewShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where createdBy does not contain DEFAULT_CREATED_BY
        defaultUserReviewShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the userReviewList where createdBy does not contain UPDATED_CREATED_BY
        defaultUserReviewShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultUserReviewShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the userReviewList where updatedAt equals to UPDATED_UPDATED_AT
        defaultUserReviewShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultUserReviewShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the userReviewList where updatedAt equals to UPDATED_UPDATED_AT
        defaultUserReviewShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedAt is not null
        defaultUserReviewShouldBeFound("updatedAt.specified=true");

        // Get all the userReviewList where updatedAt is null
        defaultUserReviewShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultUserReviewShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the userReviewList where updatedBy equals to UPDATED_UPDATED_BY
        defaultUserReviewShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultUserReviewShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the userReviewList where updatedBy equals to UPDATED_UPDATED_BY
        defaultUserReviewShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedBy is not null
        defaultUserReviewShouldBeFound("updatedBy.specified=true");

        // Get all the userReviewList where updatedBy is null
        defaultUserReviewShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedBy contains DEFAULT_UPDATED_BY
        defaultUserReviewShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the userReviewList where updatedBy contains UPDATED_UPDATED_BY
        defaultUserReviewShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultUserReviewShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the userReviewList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultUserReviewShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserReviewsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where active equals to DEFAULT_ACTIVE
        defaultUserReviewShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the userReviewList where active equals to UPDATED_ACTIVE
        defaultUserReviewShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllUserReviewsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultUserReviewShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the userReviewList where active equals to UPDATED_ACTIVE
        defaultUserReviewShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllUserReviewsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        // Get all the userReviewList where active is not null
        defaultUserReviewShouldBeFound("active.specified=true");

        // Get all the userReviewList where active is null
        defaultUserReviewShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllUserReviewsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userReviewRepository.saveAndFlush(userReview);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        userReview.setUser(user);
        userReviewRepository.saveAndFlush(userReview);
        Long userId = user.getId();
        // Get all the userReviewList where user equals to userId
        defaultUserReviewShouldBeFound("userId.equals=" + userId);

        // Get all the userReviewList where user equals to (userId + 1)
        defaultUserReviewShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserReviewShouldBeFound(String filter) throws Exception {
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userReview.getId().intValue())))
            .andExpect(jsonPath("$.[*].timesTamp").value(hasItem(DEFAULT_TIMES_TAMP.toString())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].review").value(hasItem(DEFAULT_REVIEW)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserReviewShouldNotBeFound(String filter) throws Exception {
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserReview() throws Exception {
        // Get the userReview
        restUserReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserReview() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();

        // Update the userReview
        UserReview updatedUserReview = userReviewRepository.findById(userReview.getId()).get();
        // Disconnect from session so that the updates on updatedUserReview are not directly saved in db
        em.detach(updatedUserReview);
        updatedUserReview
            .timesTamp(UPDATED_TIMES_TAMP)
            .rating(UPDATED_RATING)
            .review(UPDATED_REVIEW)
            .category(UPDATED_CATEGORY)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(updatedUserReview);

        restUserReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userReviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
        UserReview testUserReview = userReviewList.get(userReviewList.size() - 1);
        assertThat(testUserReview.getTimesTamp()).isEqualTo(UPDATED_TIMES_TAMP);
        assertThat(testUserReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testUserReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testUserReview.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testUserReview.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserReview.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserReview.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserReview.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserReview.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingUserReview() throws Exception {
        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();
        userReview.setId(count.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userReviewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserReview() throws Exception {
        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();
        userReview.setId(count.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserReview() throws Exception {
        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();
        userReview.setId(count.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userReviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserReviewWithPatch() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();

        // Update the userReview using partial update
        UserReview partialUpdatedUserReview = new UserReview();
        partialUpdatedUserReview.setId(userReview.getId());

        partialUpdatedUserReview
            .timesTamp(UPDATED_TIMES_TAMP)
            .review(UPDATED_REVIEW)
            .createdAt(UPDATED_CREATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);

        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserReview))
            )
            .andExpect(status().isOk());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
        UserReview testUserReview = userReviewList.get(userReviewList.size() - 1);
        assertThat(testUserReview.getTimesTamp()).isEqualTo(UPDATED_TIMES_TAMP);
        assertThat(testUserReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testUserReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testUserReview.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testUserReview.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserReview.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testUserReview.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserReview.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserReview.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateUserReviewWithPatch() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();

        // Update the userReview using partial update
        UserReview partialUpdatedUserReview = new UserReview();
        partialUpdatedUserReview.setId(userReview.getId());

        partialUpdatedUserReview
            .timesTamp(UPDATED_TIMES_TAMP)
            .rating(UPDATED_RATING)
            .review(UPDATED_REVIEW)
            .category(UPDATED_CATEGORY)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserReview))
            )
            .andExpect(status().isOk());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
        UserReview testUserReview = userReviewList.get(userReviewList.size() - 1);
        assertThat(testUserReview.getTimesTamp()).isEqualTo(UPDATED_TIMES_TAMP);
        assertThat(testUserReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testUserReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testUserReview.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testUserReview.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserReview.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserReview.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserReview.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserReview.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingUserReview() throws Exception {
        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();
        userReview.setId(count.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userReviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserReview() throws Exception {
        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();
        userReview.setId(count.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userReviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserReview() throws Exception {
        int databaseSizeBeforeUpdate = userReviewRepository.findAll().size();
        userReview.setId(count.incrementAndGet());

        // Create the UserReview
        UserReviewDTO userReviewDTO = userReviewMapper.toDto(userReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserReviewMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userReviewDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserReview in the database
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserReview() throws Exception {
        // Initialize the database
        userReviewRepository.saveAndFlush(userReview);

        int databaseSizeBeforeDelete = userReviewRepository.findAll().size();

        // Delete the userReview
        restUserReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, userReview.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserReview> userReviewList = userReviewRepository.findAll();
        assertThat(userReviewList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
