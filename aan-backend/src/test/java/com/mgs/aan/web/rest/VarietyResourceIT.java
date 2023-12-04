package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Product;
import com.mgs.aan.domain.Variety;
import com.mgs.aan.repository.VarietyRepository;
import com.mgs.aan.service.criteria.VarietyCriteria;
import com.mgs.aan.service.dto.VarietyDTO;
import com.mgs.aan.service.mapper.VarietyMapper;
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
 * Integration tests for the {@link VarietyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VarietyResourceIT {

    private static final String DEFAULT_VARIETY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VARIETY_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/varieties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VarietyRepository varietyRepository;

    @Autowired
    private VarietyMapper varietyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVarietyMockMvc;

    private Variety variety;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Variety createEntity(EntityManager em) {
        Variety variety = new Variety()
            .varietyName(DEFAULT_VARIETY_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .deleted(DEFAULT_DELETED);
        return variety;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Variety createUpdatedEntity(EntityManager em) {
        Variety variety = new Variety()
            .varietyName(UPDATED_VARIETY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deleted(UPDATED_DELETED);
        return variety;
    }

    @BeforeEach
    public void initTest() {
        variety = createEntity(em);
    }

    @Test
    @Transactional
    void createVariety() throws Exception {
        int databaseSizeBeforeCreate = varietyRepository.findAll().size();
        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);
        restVarietyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(varietyDTO)))
            .andExpect(status().isCreated());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeCreate + 1);
        Variety testVariety = varietyList.get(varietyList.size() - 1);
        assertThat(testVariety.getVarietyName()).isEqualTo(DEFAULT_VARIETY_NAME);
        assertThat(testVariety.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testVariety.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testVariety.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testVariety.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testVariety.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createVarietyWithExistingId() throws Exception {
        // Create the Variety with an existing ID
        variety.setId(1L);
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        int databaseSizeBeforeCreate = varietyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVarietyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(varietyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVarietyNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = varietyRepository.findAll().size();
        // set the field null
        variety.setVarietyName(null);

        // Create the Variety, which fails.
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        restVarietyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(varietyDTO)))
            .andExpect(status().isBadRequest());

        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = varietyRepository.findAll().size();
        // set the field null
        variety.setDeleted(null);

        // Create the Variety, which fails.
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        restVarietyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(varietyDTO)))
            .andExpect(status().isBadRequest());

        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVarieties() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList
        restVarietyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variety.getId().intValue())))
            .andExpect(jsonPath("$.[*].varietyName").value(hasItem(DEFAULT_VARIETY_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getVariety() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get the variety
        restVarietyMockMvc
            .perform(get(ENTITY_API_URL_ID, variety.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(variety.getId().intValue()))
            .andExpect(jsonPath("$.varietyName").value(DEFAULT_VARIETY_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getVarietiesByIdFiltering() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        Long id = variety.getId();

        defaultVarietyShouldBeFound("id.equals=" + id);
        defaultVarietyShouldNotBeFound("id.notEquals=" + id);

        defaultVarietyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVarietyShouldNotBeFound("id.greaterThan=" + id);

        defaultVarietyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVarietyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVarietiesByVarietyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where varietyName equals to DEFAULT_VARIETY_NAME
        defaultVarietyShouldBeFound("varietyName.equals=" + DEFAULT_VARIETY_NAME);

        // Get all the varietyList where varietyName equals to UPDATED_VARIETY_NAME
        defaultVarietyShouldNotBeFound("varietyName.equals=" + UPDATED_VARIETY_NAME);
    }

    @Test
    @Transactional
    void getAllVarietiesByVarietyNameIsInShouldWork() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where varietyName in DEFAULT_VARIETY_NAME or UPDATED_VARIETY_NAME
        defaultVarietyShouldBeFound("varietyName.in=" + DEFAULT_VARIETY_NAME + "," + UPDATED_VARIETY_NAME);

        // Get all the varietyList where varietyName equals to UPDATED_VARIETY_NAME
        defaultVarietyShouldNotBeFound("varietyName.in=" + UPDATED_VARIETY_NAME);
    }

    @Test
    @Transactional
    void getAllVarietiesByVarietyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where varietyName is not null
        defaultVarietyShouldBeFound("varietyName.specified=true");

        // Get all the varietyList where varietyName is null
        defaultVarietyShouldNotBeFound("varietyName.specified=false");
    }

    @Test
    @Transactional
    void getAllVarietiesByVarietyNameContainsSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where varietyName contains DEFAULT_VARIETY_NAME
        defaultVarietyShouldBeFound("varietyName.contains=" + DEFAULT_VARIETY_NAME);

        // Get all the varietyList where varietyName contains UPDATED_VARIETY_NAME
        defaultVarietyShouldNotBeFound("varietyName.contains=" + UPDATED_VARIETY_NAME);
    }

    @Test
    @Transactional
    void getAllVarietiesByVarietyNameNotContainsSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where varietyName does not contain DEFAULT_VARIETY_NAME
        defaultVarietyShouldNotBeFound("varietyName.doesNotContain=" + DEFAULT_VARIETY_NAME);

        // Get all the varietyList where varietyName does not contain UPDATED_VARIETY_NAME
        defaultVarietyShouldBeFound("varietyName.doesNotContain=" + UPDATED_VARIETY_NAME);
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdAt equals to DEFAULT_CREATED_AT
        defaultVarietyShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the varietyList where createdAt equals to UPDATED_CREATED_AT
        defaultVarietyShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultVarietyShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the varietyList where createdAt equals to UPDATED_CREATED_AT
        defaultVarietyShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdAt is not null
        defaultVarietyShouldBeFound("createdAt.specified=true");

        // Get all the varietyList where createdAt is null
        defaultVarietyShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdBy equals to DEFAULT_CREATED_BY
        defaultVarietyShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the varietyList where createdBy equals to UPDATED_CREATED_BY
        defaultVarietyShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultVarietyShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the varietyList where createdBy equals to UPDATED_CREATED_BY
        defaultVarietyShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdBy is not null
        defaultVarietyShouldBeFound("createdBy.specified=true");

        // Get all the varietyList where createdBy is null
        defaultVarietyShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdBy contains DEFAULT_CREATED_BY
        defaultVarietyShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the varietyList where createdBy contains UPDATED_CREATED_BY
        defaultVarietyShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where createdBy does not contain DEFAULT_CREATED_BY
        defaultVarietyShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the varietyList where createdBy does not contain UPDATED_CREATED_BY
        defaultVarietyShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultVarietyShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the varietyList where updatedAt equals to UPDATED_UPDATED_AT
        defaultVarietyShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultVarietyShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the varietyList where updatedAt equals to UPDATED_UPDATED_AT
        defaultVarietyShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedAt is not null
        defaultVarietyShouldBeFound("updatedAt.specified=true");

        // Get all the varietyList where updatedAt is null
        defaultVarietyShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultVarietyShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the varietyList where updatedBy equals to UPDATED_UPDATED_BY
        defaultVarietyShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultVarietyShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the varietyList where updatedBy equals to UPDATED_UPDATED_BY
        defaultVarietyShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedBy is not null
        defaultVarietyShouldBeFound("updatedBy.specified=true");

        // Get all the varietyList where updatedBy is null
        defaultVarietyShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedBy contains DEFAULT_UPDATED_BY
        defaultVarietyShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the varietyList where updatedBy contains UPDATED_UPDATED_BY
        defaultVarietyShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultVarietyShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the varietyList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultVarietyShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVarietiesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where deleted equals to DEFAULT_DELETED
        defaultVarietyShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the varietyList where deleted equals to UPDATED_DELETED
        defaultVarietyShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllVarietiesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultVarietyShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the varietyList where deleted equals to UPDATED_DELETED
        defaultVarietyShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllVarietiesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        // Get all the varietyList where deleted is not null
        defaultVarietyShouldBeFound("deleted.specified=true");

        // Get all the varietyList where deleted is null
        defaultVarietyShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllVarietiesByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            varietyRepository.saveAndFlush(variety);
            product = ProductResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        variety.setProduct(product);
        varietyRepository.saveAndFlush(variety);
        Long productId = product.getId();
        // Get all the varietyList where product equals to productId
        defaultVarietyShouldBeFound("productId.equals=" + productId);

        // Get all the varietyList where product equals to (productId + 1)
        defaultVarietyShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVarietyShouldBeFound(String filter) throws Exception {
        restVarietyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variety.getId().intValue())))
            .andExpect(jsonPath("$.[*].varietyName").value(hasItem(DEFAULT_VARIETY_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restVarietyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVarietyShouldNotBeFound(String filter) throws Exception {
        restVarietyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVarietyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVariety() throws Exception {
        // Get the variety
        restVarietyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVariety() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();

        // Update the variety
        Variety updatedVariety = varietyRepository.findById(variety.getId()).get();
        // Disconnect from session so that the updates on updatedVariety are not directly saved in db
        em.detach(updatedVariety);
        updatedVariety
            .varietyName(UPDATED_VARIETY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deleted(UPDATED_DELETED);
        VarietyDTO varietyDTO = varietyMapper.toDto(updatedVariety);

        restVarietyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, varietyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(varietyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
        Variety testVariety = varietyList.get(varietyList.size() - 1);
        assertThat(testVariety.getVarietyName()).isEqualTo(UPDATED_VARIETY_NAME);
        assertThat(testVariety.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testVariety.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVariety.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testVariety.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testVariety.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingVariety() throws Exception {
        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();
        variety.setId(count.incrementAndGet());

        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVarietyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, varietyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(varietyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVariety() throws Exception {
        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();
        variety.setId(count.incrementAndGet());

        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarietyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(varietyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVariety() throws Exception {
        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();
        variety.setId(count.incrementAndGet());

        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarietyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(varietyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVarietyWithPatch() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();

        // Update the variety using partial update
        Variety partialUpdatedVariety = new Variety();
        partialUpdatedVariety.setId(variety.getId());

        partialUpdatedVariety.createdAt(UPDATED_CREATED_AT);

        restVarietyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVariety.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVariety))
            )
            .andExpect(status().isOk());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
        Variety testVariety = varietyList.get(varietyList.size() - 1);
        assertThat(testVariety.getVarietyName()).isEqualTo(DEFAULT_VARIETY_NAME);
        assertThat(testVariety.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testVariety.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testVariety.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testVariety.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testVariety.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateVarietyWithPatch() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();

        // Update the variety using partial update
        Variety partialUpdatedVariety = new Variety();
        partialUpdatedVariety.setId(variety.getId());

        partialUpdatedVariety
            .varietyName(UPDATED_VARIETY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .deleted(UPDATED_DELETED);

        restVarietyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVariety.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVariety))
            )
            .andExpect(status().isOk());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
        Variety testVariety = varietyList.get(varietyList.size() - 1);
        assertThat(testVariety.getVarietyName()).isEqualTo(UPDATED_VARIETY_NAME);
        assertThat(testVariety.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testVariety.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVariety.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testVariety.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testVariety.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingVariety() throws Exception {
        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();
        variety.setId(count.incrementAndGet());

        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVarietyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, varietyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(varietyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVariety() throws Exception {
        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();
        variety.setId(count.incrementAndGet());

        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarietyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(varietyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVariety() throws Exception {
        int databaseSizeBeforeUpdate = varietyRepository.findAll().size();
        variety.setId(count.incrementAndGet());

        // Create the Variety
        VarietyDTO varietyDTO = varietyMapper.toDto(variety);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarietyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(varietyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Variety in the database
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVariety() throws Exception {
        // Initialize the database
        varietyRepository.saveAndFlush(variety);

        int databaseSizeBeforeDelete = varietyRepository.findAll().size();

        // Delete the variety
        restVarietyMockMvc
            .perform(delete(ENTITY_API_URL_ID, variety.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Variety> varietyList = varietyRepository.findAll();
        assertThat(varietyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
