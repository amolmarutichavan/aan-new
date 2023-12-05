package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.District;
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.domain.Village;
import com.mgs.aan.repository.TalukaRepository;
import com.mgs.aan.service.criteria.TalukaCriteria;
import com.mgs.aan.service.dto.TalukaDTO;
import com.mgs.aan.service.mapper.TalukaMapper;
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
 * Integration tests for the {@link TalukaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TalukaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/talukas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TalukaRepository talukaRepository;

    @Autowired
    private TalukaMapper talukaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTalukaMockMvc;

    private Taluka taluka;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Taluka createEntity(EntityManager em) {
        Taluka taluka = new Taluka()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return taluka;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Taluka createUpdatedEntity(EntityManager em) {
        Taluka taluka = new Taluka()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return taluka;
    }

    @BeforeEach
    public void initTest() {
        taluka = createEntity(em);
    }

    @Test
    @Transactional
    void createTaluka() throws Exception {
        int databaseSizeBeforeCreate = talukaRepository.findAll().size();
        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);
        restTalukaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(talukaDTO)))
            .andExpect(status().isCreated());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeCreate + 1);
        Taluka testTaluka = talukaList.get(talukaList.size() - 1);
        assertThat(testTaluka.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaluka.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTaluka.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTaluka.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testTaluka.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testTaluka.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createTalukaWithExistingId() throws Exception {
        // Create the Taluka with an existing ID
        taluka.setId(1L);
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        int databaseSizeBeforeCreate = talukaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTalukaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(talukaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = talukaRepository.findAll().size();
        // set the field null
        taluka.setActive(null);

        // Create the Taluka, which fails.
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        restTalukaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(talukaDTO)))
            .andExpect(status().isBadRequest());

        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTalukas() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList
        restTalukaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taluka.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getTaluka() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get the taluka
        restTalukaMockMvc
            .perform(get(ENTITY_API_URL_ID, taluka.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taluka.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getTalukasByIdFiltering() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        Long id = taluka.getId();

        defaultTalukaShouldBeFound("id.equals=" + id);
        defaultTalukaShouldNotBeFound("id.notEquals=" + id);

        defaultTalukaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTalukaShouldNotBeFound("id.greaterThan=" + id);

        defaultTalukaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTalukaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTalukasByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where name equals to DEFAULT_NAME
        defaultTalukaShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the talukaList where name equals to UPDATED_NAME
        defaultTalukaShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTalukasByNameIsInShouldWork() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTalukaShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the talukaList where name equals to UPDATED_NAME
        defaultTalukaShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTalukasByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where name is not null
        defaultTalukaShouldBeFound("name.specified=true");

        // Get all the talukaList where name is null
        defaultTalukaShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTalukasByNameContainsSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where name contains DEFAULT_NAME
        defaultTalukaShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the talukaList where name contains UPDATED_NAME
        defaultTalukaShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTalukasByNameNotContainsSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where name does not contain DEFAULT_NAME
        defaultTalukaShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the talukaList where name does not contain UPDATED_NAME
        defaultTalukaShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdAt equals to DEFAULT_CREATED_AT
        defaultTalukaShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the talukaList where createdAt equals to UPDATED_CREATED_AT
        defaultTalukaShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultTalukaShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the talukaList where createdAt equals to UPDATED_CREATED_AT
        defaultTalukaShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdAt is not null
        defaultTalukaShouldBeFound("createdAt.specified=true");

        // Get all the talukaList where createdAt is null
        defaultTalukaShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdBy equals to DEFAULT_CREATED_BY
        defaultTalukaShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the talukaList where createdBy equals to UPDATED_CREATED_BY
        defaultTalukaShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTalukaShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the talukaList where createdBy equals to UPDATED_CREATED_BY
        defaultTalukaShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdBy is not null
        defaultTalukaShouldBeFound("createdBy.specified=true");

        // Get all the talukaList where createdBy is null
        defaultTalukaShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdBy contains DEFAULT_CREATED_BY
        defaultTalukaShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the talukaList where createdBy contains UPDATED_CREATED_BY
        defaultTalukaShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTalukaShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the talukaList where createdBy does not contain UPDATED_CREATED_BY
        defaultTalukaShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultTalukaShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the talukaList where updatedAt equals to UPDATED_UPDATED_AT
        defaultTalukaShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultTalukaShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the talukaList where updatedAt equals to UPDATED_UPDATED_AT
        defaultTalukaShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedAt is not null
        defaultTalukaShouldBeFound("updatedAt.specified=true");

        // Get all the talukaList where updatedAt is null
        defaultTalukaShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultTalukaShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the talukaList where updatedBy equals to UPDATED_UPDATED_BY
        defaultTalukaShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultTalukaShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the talukaList where updatedBy equals to UPDATED_UPDATED_BY
        defaultTalukaShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedBy is not null
        defaultTalukaShouldBeFound("updatedBy.specified=true");

        // Get all the talukaList where updatedBy is null
        defaultTalukaShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedBy contains DEFAULT_UPDATED_BY
        defaultTalukaShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the talukaList where updatedBy contains UPDATED_UPDATED_BY
        defaultTalukaShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultTalukaShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the talukaList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultTalukaShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTalukasByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where active equals to DEFAULT_ACTIVE
        defaultTalukaShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the talukaList where active equals to UPDATED_ACTIVE
        defaultTalukaShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTalukasByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultTalukaShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the talukaList where active equals to UPDATED_ACTIVE
        defaultTalukaShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTalukasByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        // Get all the talukaList where active is not null
        defaultTalukaShouldBeFound("active.specified=true");

        // Get all the talukaList where active is null
        defaultTalukaShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllTalukasByVillageIsEqualToSomething() throws Exception {
        Village village;
        if (TestUtil.findAll(em, Village.class).isEmpty()) {
            talukaRepository.saveAndFlush(taluka);
            village = VillageResourceIT.createEntity(em);
        } else {
            village = TestUtil.findAll(em, Village.class).get(0);
        }
        em.persist(village);
        em.flush();
        taluka.addVillage(village);
        talukaRepository.saveAndFlush(taluka);
        Long villageId = village.getId();
        // Get all the talukaList where village equals to villageId
        defaultTalukaShouldBeFound("villageId.equals=" + villageId);

        // Get all the talukaList where village equals to (villageId + 1)
        defaultTalukaShouldNotBeFound("villageId.equals=" + (villageId + 1));
    }

    @Test
    @Transactional
    void getAllTalukasByUserLocationIsEqualToSomething() throws Exception {
        UserLocation userLocation;
        if (TestUtil.findAll(em, UserLocation.class).isEmpty()) {
            talukaRepository.saveAndFlush(taluka);
            userLocation = UserLocationResourceIT.createEntity(em);
        } else {
            userLocation = TestUtil.findAll(em, UserLocation.class).get(0);
        }
        em.persist(userLocation);
        em.flush();
        taluka.addUserLocation(userLocation);
        talukaRepository.saveAndFlush(taluka);
        Long userLocationId = userLocation.getId();
        // Get all the talukaList where userLocation equals to userLocationId
        defaultTalukaShouldBeFound("userLocationId.equals=" + userLocationId);

        // Get all the talukaList where userLocation equals to (userLocationId + 1)
        defaultTalukaShouldNotBeFound("userLocationId.equals=" + (userLocationId + 1));
    }

    @Test
    @Transactional
    void getAllTalukasByDistrictIsEqualToSomething() throws Exception {
        District district;
        if (TestUtil.findAll(em, District.class).isEmpty()) {
            talukaRepository.saveAndFlush(taluka);
            district = DistrictResourceIT.createEntity(em);
        } else {
            district = TestUtil.findAll(em, District.class).get(0);
        }
        em.persist(district);
        em.flush();
        taluka.setDistrict(district);
        talukaRepository.saveAndFlush(taluka);
        Long districtId = district.getId();
        // Get all the talukaList where district equals to districtId
        defaultTalukaShouldBeFound("districtId.equals=" + districtId);

        // Get all the talukaList where district equals to (districtId + 1)
        defaultTalukaShouldNotBeFound("districtId.equals=" + (districtId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTalukaShouldBeFound(String filter) throws Exception {
        restTalukaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taluka.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restTalukaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTalukaShouldNotBeFound(String filter) throws Exception {
        restTalukaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTalukaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaluka() throws Exception {
        // Get the taluka
        restTalukaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaluka() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();

        // Update the taluka
        Taluka updatedTaluka = talukaRepository.findById(taluka.getId()).get();
        // Disconnect from session so that the updates on updatedTaluka are not directly saved in db
        em.detach(updatedTaluka);
        updatedTaluka
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        TalukaDTO talukaDTO = talukaMapper.toDto(updatedTaluka);

        restTalukaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, talukaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(talukaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
        Taluka testTaluka = talukaList.get(talukaList.size() - 1);
        assertThat(testTaluka.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaluka.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTaluka.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTaluka.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testTaluka.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testTaluka.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingTaluka() throws Exception {
        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();
        taluka.setId(count.incrementAndGet());

        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTalukaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, talukaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(talukaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaluka() throws Exception {
        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();
        taluka.setId(count.incrementAndGet());

        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTalukaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(talukaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaluka() throws Exception {
        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();
        taluka.setId(count.incrementAndGet());

        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTalukaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(talukaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTalukaWithPatch() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();

        // Update the taluka using partial update
        Taluka partialUpdatedTaluka = new Taluka();
        partialUpdatedTaluka.setId(taluka.getId());

        partialUpdatedTaluka
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restTalukaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaluka.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaluka))
            )
            .andExpect(status().isOk());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
        Taluka testTaluka = talukaList.get(talukaList.size() - 1);
        assertThat(testTaluka.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaluka.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTaluka.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTaluka.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testTaluka.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testTaluka.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateTalukaWithPatch() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();

        // Update the taluka using partial update
        Taluka partialUpdatedTaluka = new Taluka();
        partialUpdatedTaluka.setId(taluka.getId());

        partialUpdatedTaluka
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restTalukaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaluka.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaluka))
            )
            .andExpect(status().isOk());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
        Taluka testTaluka = talukaList.get(talukaList.size() - 1);
        assertThat(testTaluka.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaluka.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTaluka.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTaluka.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testTaluka.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testTaluka.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingTaluka() throws Exception {
        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();
        taluka.setId(count.incrementAndGet());

        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTalukaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, talukaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(talukaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaluka() throws Exception {
        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();
        taluka.setId(count.incrementAndGet());

        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTalukaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(talukaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaluka() throws Exception {
        int databaseSizeBeforeUpdate = talukaRepository.findAll().size();
        taluka.setId(count.incrementAndGet());

        // Create the Taluka
        TalukaDTO talukaDTO = talukaMapper.toDto(taluka);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTalukaMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(talukaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Taluka in the database
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaluka() throws Exception {
        // Initialize the database
        talukaRepository.saveAndFlush(taluka);

        int databaseSizeBeforeDelete = talukaRepository.findAll().size();

        // Delete the taluka
        restTalukaMockMvc
            .perform(delete(ENTITY_API_URL_ID, taluka.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Taluka> talukaList = talukaRepository.findAll();
        assertThat(talukaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
