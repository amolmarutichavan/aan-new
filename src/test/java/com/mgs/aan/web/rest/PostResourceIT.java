package com.mgs.aan.web.rest;

import static com.mgs.aan.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Post;
import com.mgs.aan.domain.PostImage;
import com.mgs.aan.domain.Product;
import com.mgs.aan.domain.User;
import com.mgs.aan.domain.enumeration.PostType;
import com.mgs.aan.repository.PostRepository;
import com.mgs.aan.service.PostService;
import com.mgs.aan.service.criteria.PostCriteria;
import com.mgs.aan.service.dto.PostDTO;
import com.mgs.aan.service.mapper.PostMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PostResourceIT {

    private static final PostType DEFAULT_POST_TYPE = PostType.BUYER;
    private static final PostType UPDATED_POST_TYPE = PostType.SELLER;

    private static final Long DEFAULT_QUANTITY = 1L;
    private static final Long UPDATED_QUANTITY = 2L;
    private static final Long SMALLER_QUANTITY = 1L - 1L;

    private static final Double DEFAULT_UNIT = 1D;
    private static final Double UPDATED_UNIT = 2D;
    private static final Double SMALLER_UNIT = 1D - 1D;

    private static final BigDecimal DEFAULT_MIN_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_MIN_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_MAX_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_MAX_PRICE = new BigDecimal(1 - 1);

    private static final LocalDate DEFAULT_TARGET_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TARGET_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TARGET_DATE = LocalDate.ofEpochDay(-1L);

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

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PostRepository postRepository;

    @Mock
    private PostRepository postRepositoryMock;

    @Autowired
    private PostMapper postMapper;

    @Mock
    private PostService postServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostMockMvc;

    private Post post;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .postType(DEFAULT_POST_TYPE)
            .quantity(DEFAULT_QUANTITY)
            .unit(DEFAULT_UNIT)
            .minPrice(DEFAULT_MIN_PRICE)
            .maxPrice(DEFAULT_MAX_PRICE)
            .targetDate(DEFAULT_TARGET_DATE)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return post;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity(EntityManager em) {
        Post post = new Post()
            .postType(UPDATED_POST_TYPE)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .targetDate(UPDATED_TARGET_DATE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return post;
    }

    @BeforeEach
    public void initTest() {
        post = createEntity(em);
    }

    @Test
    @Transactional
    void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();
        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);
        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostType()).isEqualTo(DEFAULT_POST_TYPE);
        assertThat(testPost.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testPost.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testPost.getMinPrice()).isEqualByComparingTo(DEFAULT_MIN_PRICE);
        assertThat(testPost.getMaxPrice()).isEqualByComparingTo(DEFAULT_MAX_PRICE);
        assertThat(testPost.getTargetDate()).isEqualTo(DEFAULT_TARGET_DATE);
        assertThat(testPost.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPost.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPost.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testPost.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPost.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createPostWithExistingId() throws Exception {
        // Create the Post with an existing ID
        post.setId(1L);
        PostDTO postDTO = postMapper.toDto(post);

        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTargetDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setTargetDate(null);

        // Create the Post, which fails.
        PostDTO postDTO = postMapper.toDto(post);

        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setActive(null);

        // Create the Post, which fails.
        PostDTO postDTO = postMapper.toDto(post);

        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].postType").value(hasItem(DEFAULT_POST_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].minPrice").value(hasItem(sameNumber(DEFAULT_MIN_PRICE))))
            .andExpect(jsonPath("$.[*].maxPrice").value(hasItem(sameNumber(DEFAULT_MAX_PRICE))))
            .andExpect(jsonPath("$.[*].targetDate").value(hasItem(DEFAULT_TARGET_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostsWithEagerRelationshipsIsEnabled() throws Exception {
        when(postServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(postServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(postServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPostMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(postRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc
            .perform(get(ENTITY_API_URL_ID, post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.postType").value(DEFAULT_POST_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.intValue()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.doubleValue()))
            .andExpect(jsonPath("$.minPrice").value(sameNumber(DEFAULT_MIN_PRICE)))
            .andExpect(jsonPath("$.maxPrice").value(sameNumber(DEFAULT_MAX_PRICE)))
            .andExpect(jsonPath("$.targetDate").value(DEFAULT_TARGET_DATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getPostsByIdFiltering() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        Long id = post.getId();

        defaultPostShouldBeFound("id.equals=" + id);
        defaultPostShouldNotBeFound("id.notEquals=" + id);

        defaultPostShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.greaterThan=" + id);

        defaultPostShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostsByPostTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where postType equals to DEFAULT_POST_TYPE
        defaultPostShouldBeFound("postType.equals=" + DEFAULT_POST_TYPE);

        // Get all the postList where postType equals to UPDATED_POST_TYPE
        defaultPostShouldNotBeFound("postType.equals=" + UPDATED_POST_TYPE);
    }

    @Test
    @Transactional
    void getAllPostsByPostTypeIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where postType in DEFAULT_POST_TYPE or UPDATED_POST_TYPE
        defaultPostShouldBeFound("postType.in=" + DEFAULT_POST_TYPE + "," + UPDATED_POST_TYPE);

        // Get all the postList where postType equals to UPDATED_POST_TYPE
        defaultPostShouldNotBeFound("postType.in=" + UPDATED_POST_TYPE);
    }

    @Test
    @Transactional
    void getAllPostsByPostTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where postType is not null
        defaultPostShouldBeFound("postType.specified=true");

        // Get all the postList where postType is null
        defaultPostShouldNotBeFound("postType.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity equals to DEFAULT_QUANTITY
        defaultPostShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the postList where quantity equals to UPDATED_QUANTITY
        defaultPostShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultPostShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the postList where quantity equals to UPDATED_QUANTITY
        defaultPostShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity is not null
        defaultPostShouldBeFound("quantity.specified=true");

        // Get all the postList where quantity is null
        defaultPostShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultPostShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the postList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultPostShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultPostShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the postList where quantity is less than or equal to SMALLER_QUANTITY
        defaultPostShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity is less than DEFAULT_QUANTITY
        defaultPostShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the postList where quantity is less than UPDATED_QUANTITY
        defaultPostShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPostsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where quantity is greater than DEFAULT_QUANTITY
        defaultPostShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the postList where quantity is greater than SMALLER_QUANTITY
        defaultPostShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit equals to DEFAULT_UNIT
        defaultPostShouldBeFound("unit.equals=" + DEFAULT_UNIT);

        // Get all the postList where unit equals to UPDATED_UNIT
        defaultPostShouldNotBeFound("unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit in DEFAULT_UNIT or UPDATED_UNIT
        defaultPostShouldBeFound("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT);

        // Get all the postList where unit equals to UPDATED_UNIT
        defaultPostShouldNotBeFound("unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit is not null
        defaultPostShouldBeFound("unit.specified=true");

        // Get all the postList where unit is null
        defaultPostShouldNotBeFound("unit.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit is greater than or equal to DEFAULT_UNIT
        defaultPostShouldBeFound("unit.greaterThanOrEqual=" + DEFAULT_UNIT);

        // Get all the postList where unit is greater than or equal to UPDATED_UNIT
        defaultPostShouldNotBeFound("unit.greaterThanOrEqual=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit is less than or equal to DEFAULT_UNIT
        defaultPostShouldBeFound("unit.lessThanOrEqual=" + DEFAULT_UNIT);

        // Get all the postList where unit is less than or equal to SMALLER_UNIT
        defaultPostShouldNotBeFound("unit.lessThanOrEqual=" + SMALLER_UNIT);
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit is less than DEFAULT_UNIT
        defaultPostShouldNotBeFound("unit.lessThan=" + DEFAULT_UNIT);

        // Get all the postList where unit is less than UPDATED_UNIT
        defaultPostShouldBeFound("unit.lessThan=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllPostsByUnitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where unit is greater than DEFAULT_UNIT
        defaultPostShouldNotBeFound("unit.greaterThan=" + DEFAULT_UNIT);

        // Get all the postList where unit is greater than SMALLER_UNIT
        defaultPostShouldBeFound("unit.greaterThan=" + SMALLER_UNIT);
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice equals to DEFAULT_MIN_PRICE
        defaultPostShouldBeFound("minPrice.equals=" + DEFAULT_MIN_PRICE);

        // Get all the postList where minPrice equals to UPDATED_MIN_PRICE
        defaultPostShouldNotBeFound("minPrice.equals=" + UPDATED_MIN_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice in DEFAULT_MIN_PRICE or UPDATED_MIN_PRICE
        defaultPostShouldBeFound("minPrice.in=" + DEFAULT_MIN_PRICE + "," + UPDATED_MIN_PRICE);

        // Get all the postList where minPrice equals to UPDATED_MIN_PRICE
        defaultPostShouldNotBeFound("minPrice.in=" + UPDATED_MIN_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice is not null
        defaultPostShouldBeFound("minPrice.specified=true");

        // Get all the postList where minPrice is null
        defaultPostShouldNotBeFound("minPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice is greater than or equal to DEFAULT_MIN_PRICE
        defaultPostShouldBeFound("minPrice.greaterThanOrEqual=" + DEFAULT_MIN_PRICE);

        // Get all the postList where minPrice is greater than or equal to UPDATED_MIN_PRICE
        defaultPostShouldNotBeFound("minPrice.greaterThanOrEqual=" + UPDATED_MIN_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice is less than or equal to DEFAULT_MIN_PRICE
        defaultPostShouldBeFound("minPrice.lessThanOrEqual=" + DEFAULT_MIN_PRICE);

        // Get all the postList where minPrice is less than or equal to SMALLER_MIN_PRICE
        defaultPostShouldNotBeFound("minPrice.lessThanOrEqual=" + SMALLER_MIN_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice is less than DEFAULT_MIN_PRICE
        defaultPostShouldNotBeFound("minPrice.lessThan=" + DEFAULT_MIN_PRICE);

        // Get all the postList where minPrice is less than UPDATED_MIN_PRICE
        defaultPostShouldBeFound("minPrice.lessThan=" + UPDATED_MIN_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMinPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where minPrice is greater than DEFAULT_MIN_PRICE
        defaultPostShouldNotBeFound("minPrice.greaterThan=" + DEFAULT_MIN_PRICE);

        // Get all the postList where minPrice is greater than SMALLER_MIN_PRICE
        defaultPostShouldBeFound("minPrice.greaterThan=" + SMALLER_MIN_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice equals to DEFAULT_MAX_PRICE
        defaultPostShouldBeFound("maxPrice.equals=" + DEFAULT_MAX_PRICE);

        // Get all the postList where maxPrice equals to UPDATED_MAX_PRICE
        defaultPostShouldNotBeFound("maxPrice.equals=" + UPDATED_MAX_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice in DEFAULT_MAX_PRICE or UPDATED_MAX_PRICE
        defaultPostShouldBeFound("maxPrice.in=" + DEFAULT_MAX_PRICE + "," + UPDATED_MAX_PRICE);

        // Get all the postList where maxPrice equals to UPDATED_MAX_PRICE
        defaultPostShouldNotBeFound("maxPrice.in=" + UPDATED_MAX_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice is not null
        defaultPostShouldBeFound("maxPrice.specified=true");

        // Get all the postList where maxPrice is null
        defaultPostShouldNotBeFound("maxPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice is greater than or equal to DEFAULT_MAX_PRICE
        defaultPostShouldBeFound("maxPrice.greaterThanOrEqual=" + DEFAULT_MAX_PRICE);

        // Get all the postList where maxPrice is greater than or equal to UPDATED_MAX_PRICE
        defaultPostShouldNotBeFound("maxPrice.greaterThanOrEqual=" + UPDATED_MAX_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice is less than or equal to DEFAULT_MAX_PRICE
        defaultPostShouldBeFound("maxPrice.lessThanOrEqual=" + DEFAULT_MAX_PRICE);

        // Get all the postList where maxPrice is less than or equal to SMALLER_MAX_PRICE
        defaultPostShouldNotBeFound("maxPrice.lessThanOrEqual=" + SMALLER_MAX_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice is less than DEFAULT_MAX_PRICE
        defaultPostShouldNotBeFound("maxPrice.lessThan=" + DEFAULT_MAX_PRICE);

        // Get all the postList where maxPrice is less than UPDATED_MAX_PRICE
        defaultPostShouldBeFound("maxPrice.lessThan=" + UPDATED_MAX_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByMaxPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where maxPrice is greater than DEFAULT_MAX_PRICE
        defaultPostShouldNotBeFound("maxPrice.greaterThan=" + DEFAULT_MAX_PRICE);

        // Get all the postList where maxPrice is greater than SMALLER_MAX_PRICE
        defaultPostShouldBeFound("maxPrice.greaterThan=" + SMALLER_MAX_PRICE);
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate equals to DEFAULT_TARGET_DATE
        defaultPostShouldBeFound("targetDate.equals=" + DEFAULT_TARGET_DATE);

        // Get all the postList where targetDate equals to UPDATED_TARGET_DATE
        defaultPostShouldNotBeFound("targetDate.equals=" + UPDATED_TARGET_DATE);
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate in DEFAULT_TARGET_DATE or UPDATED_TARGET_DATE
        defaultPostShouldBeFound("targetDate.in=" + DEFAULT_TARGET_DATE + "," + UPDATED_TARGET_DATE);

        // Get all the postList where targetDate equals to UPDATED_TARGET_DATE
        defaultPostShouldNotBeFound("targetDate.in=" + UPDATED_TARGET_DATE);
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate is not null
        defaultPostShouldBeFound("targetDate.specified=true");

        // Get all the postList where targetDate is null
        defaultPostShouldNotBeFound("targetDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate is greater than or equal to DEFAULT_TARGET_DATE
        defaultPostShouldBeFound("targetDate.greaterThanOrEqual=" + DEFAULT_TARGET_DATE);

        // Get all the postList where targetDate is greater than or equal to UPDATED_TARGET_DATE
        defaultPostShouldNotBeFound("targetDate.greaterThanOrEqual=" + UPDATED_TARGET_DATE);
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate is less than or equal to DEFAULT_TARGET_DATE
        defaultPostShouldBeFound("targetDate.lessThanOrEqual=" + DEFAULT_TARGET_DATE);

        // Get all the postList where targetDate is less than or equal to SMALLER_TARGET_DATE
        defaultPostShouldNotBeFound("targetDate.lessThanOrEqual=" + SMALLER_TARGET_DATE);
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate is less than DEFAULT_TARGET_DATE
        defaultPostShouldNotBeFound("targetDate.lessThan=" + DEFAULT_TARGET_DATE);

        // Get all the postList where targetDate is less than UPDATED_TARGET_DATE
        defaultPostShouldBeFound("targetDate.lessThan=" + UPDATED_TARGET_DATE);
    }

    @Test
    @Transactional
    void getAllPostsByTargetDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where targetDate is greater than DEFAULT_TARGET_DATE
        defaultPostShouldNotBeFound("targetDate.greaterThan=" + DEFAULT_TARGET_DATE);

        // Get all the postList where targetDate is greater than SMALLER_TARGET_DATE
        defaultPostShouldBeFound("targetDate.greaterThan=" + SMALLER_TARGET_DATE);
    }

    @Test
    @Transactional
    void getAllPostsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdAt equals to DEFAULT_CREATED_AT
        defaultPostShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the postList where createdAt equals to UPDATED_CREATED_AT
        defaultPostShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPostsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultPostShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the postList where createdAt equals to UPDATED_CREATED_AT
        defaultPostShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPostsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdAt is not null
        defaultPostShouldBeFound("createdAt.specified=true");

        // Get all the postList where createdAt is null
        defaultPostShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdBy equals to DEFAULT_CREATED_BY
        defaultPostShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the postList where createdBy equals to UPDATED_CREATED_BY
        defaultPostShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultPostShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the postList where createdBy equals to UPDATED_CREATED_BY
        defaultPostShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdBy is not null
        defaultPostShouldBeFound("createdBy.specified=true");

        // Get all the postList where createdBy is null
        defaultPostShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdBy contains DEFAULT_CREATED_BY
        defaultPostShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the postList where createdBy contains UPDATED_CREATED_BY
        defaultPostShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createdBy does not contain DEFAULT_CREATED_BY
        defaultPostShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the postList where createdBy does not contain UPDATED_CREATED_BY
        defaultPostShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultPostShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the postList where updatedAt equals to UPDATED_UPDATED_AT
        defaultPostShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultPostShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the postList where updatedAt equals to UPDATED_UPDATED_AT
        defaultPostShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedAt is not null
        defaultPostShouldBeFound("updatedAt.specified=true");

        // Get all the postList where updatedAt is null
        defaultPostShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultPostShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the postList where updatedBy equals to UPDATED_UPDATED_BY
        defaultPostShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultPostShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the postList where updatedBy equals to UPDATED_UPDATED_BY
        defaultPostShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedBy is not null
        defaultPostShouldBeFound("updatedBy.specified=true");

        // Get all the postList where updatedBy is null
        defaultPostShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedBy contains DEFAULT_UPDATED_BY
        defaultPostShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the postList where updatedBy contains UPDATED_UPDATED_BY
        defaultPostShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultPostShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the postList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultPostShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where active equals to DEFAULT_ACTIVE
        defaultPostShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the postList where active equals to UPDATED_ACTIVE
        defaultPostShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPostsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultPostShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the postList where active equals to UPDATED_ACTIVE
        defaultPostShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPostsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where active is not null
        defaultPostShouldBeFound("active.specified=true");

        // Get all the postList where active is null
        defaultPostShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByPostImageIsEqualToSomething() throws Exception {
        PostImage postImage;
        if (TestUtil.findAll(em, PostImage.class).isEmpty()) {
            postRepository.saveAndFlush(post);
            postImage = PostImageResourceIT.createEntity(em);
        } else {
            postImage = TestUtil.findAll(em, PostImage.class).get(0);
        }
        em.persist(postImage);
        em.flush();
        post.addPostImage(postImage);
        postRepository.saveAndFlush(post);
        Long postImageId = postImage.getId();
        // Get all the postList where postImage equals to postImageId
        defaultPostShouldBeFound("postImageId.equals=" + postImageId);

        // Get all the postList where postImage equals to (postImageId + 1)
        defaultPostShouldNotBeFound("postImageId.equals=" + (postImageId + 1));
    }

    @Test
    @Transactional
    void getAllPostsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            postRepository.saveAndFlush(post);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        post.setUser(user);
        postRepository.saveAndFlush(post);
        Long userId = user.getId();
        // Get all the postList where user equals to userId
        defaultPostShouldBeFound("userId.equals=" + userId);

        // Get all the postList where user equals to (userId + 1)
        defaultPostShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllPostsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            postRepository.saveAndFlush(post);
            product = ProductResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        post.addProduct(product);
        postRepository.saveAndFlush(post);
        Long productId = product.getId();
        // Get all the postList where product equals to productId
        defaultPostShouldBeFound("productId.equals=" + productId);

        // Get all the postList where product equals to (productId + 1)
        defaultPostShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostShouldBeFound(String filter) throws Exception {
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].postType").value(hasItem(DEFAULT_POST_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].minPrice").value(hasItem(sameNumber(DEFAULT_MIN_PRICE))))
            .andExpect(jsonPath("$.[*].maxPrice").value(hasItem(sameNumber(DEFAULT_MAX_PRICE))))
            .andExpect(jsonPath("$.[*].targetDate").value(hasItem(DEFAULT_TARGET_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostShouldNotBeFound(String filter) throws Exception {
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .postType(UPDATED_POST_TYPE)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .targetDate(UPDATED_TARGET_DATE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        PostDTO postDTO = postMapper.toDto(updatedPost);

        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postDTO))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostType()).isEqualTo(UPDATED_POST_TYPE);
        assertThat(testPost.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testPost.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testPost.getMinPrice()).isEqualByComparingTo(UPDATED_MIN_PRICE);
        assertThat(testPost.getMaxPrice()).isEqualByComparingTo(UPDATED_MAX_PRICE);
        assertThat(testPost.getTargetDate()).isEqualTo(UPDATED_TARGET_DATE);
        assertThat(testPost.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPost.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPost.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPost.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPost.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(postDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost.active(UPDATED_ACTIVE);

        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostType()).isEqualTo(DEFAULT_POST_TYPE);
        assertThat(testPost.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testPost.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testPost.getMinPrice()).isEqualByComparingTo(DEFAULT_MIN_PRICE);
        assertThat(testPost.getMaxPrice()).isEqualByComparingTo(DEFAULT_MAX_PRICE);
        assertThat(testPost.getTargetDate()).isEqualTo(DEFAULT_TARGET_DATE);
        assertThat(testPost.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPost.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPost.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testPost.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPost.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost
            .postType(UPDATED_POST_TYPE)
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .minPrice(UPDATED_MIN_PRICE)
            .maxPrice(UPDATED_MAX_PRICE)
            .targetDate(UPDATED_TARGET_DATE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostType()).isEqualTo(UPDATED_POST_TYPE);
        assertThat(testPost.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testPost.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testPost.getMinPrice()).isEqualByComparingTo(UPDATED_MIN_PRICE);
        assertThat(testPost.getMaxPrice()).isEqualByComparingTo(UPDATED_MAX_PRICE);
        assertThat(testPost.getTargetDate()).isEqualTo(UPDATED_TARGET_DATE);
        assertThat(testPost.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPost.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPost.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPost.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPost.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(postDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // Create the Post
        PostDTO postDTO = postMapper.toDto(post);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(postDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Delete the post
        restPostMockMvc
            .perform(delete(ENTITY_API_URL_ID, post.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
