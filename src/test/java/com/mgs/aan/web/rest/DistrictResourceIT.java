package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.District;
import com.mgs.aan.domain.State;
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.repository.DistrictRepository;
import com.mgs.aan.service.criteria.DistrictCriteria;
import com.mgs.aan.service.dto.DistrictDTO;
import com.mgs.aan.service.mapper.DistrictMapper;
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
 * Integration tests for the {@link DistrictResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DistrictResourceIT {

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

    private static final String ENTITY_API_URL = "/api/districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private DistrictMapper districtMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDistrictMockMvc;

    private District district;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createEntity(EntityManager em) {
        District district = new District()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return district;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createUpdatedEntity(EntityManager em) {
        District district = new District()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return district;
    }

    @BeforeEach
    public void initTest() {
        district = createEntity(em);
    }

    @Test
    @Transactional
    void createDistrict() throws Exception {
        int databaseSizeBeforeCreate = districtRepository.findAll().size();
        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);
        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(districtDTO)))
            .andExpect(status().isCreated());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeCreate + 1);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDistrict.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testDistrict.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDistrict.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testDistrict.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testDistrict.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createDistrictWithExistingId() throws Exception {
        // Create the District with an existing ID
        district.setId(1L);
        DistrictDTO districtDTO = districtMapper.toDto(district);

        int databaseSizeBeforeCreate = districtRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(districtDTO)))
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = districtRepository.findAll().size();
        // set the field null
        district.setActive(null);

        // Create the District, which fails.
        DistrictDTO districtDTO = districtMapper.toDto(district);

        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(districtDTO)))
            .andExpect(status().isBadRequest());

        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDistricts() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(district.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get the district
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL_ID, district.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(district.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getDistrictsByIdFiltering() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        Long id = district.getId();

        defaultDistrictShouldBeFound("id.equals=" + id);
        defaultDistrictShouldNotBeFound("id.notEquals=" + id);

        defaultDistrictShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDistrictShouldNotBeFound("id.greaterThan=" + id);

        defaultDistrictShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDistrictShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where name equals to DEFAULT_NAME
        defaultDistrictShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the districtList where name equals to UPDATED_NAME
        defaultDistrictShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDistrictShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the districtList where name equals to UPDATED_NAME
        defaultDistrictShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where name is not null
        defaultDistrictShouldBeFound("name.specified=true");

        // Get all the districtList where name is null
        defaultDistrictShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByNameContainsSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where name contains DEFAULT_NAME
        defaultDistrictShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the districtList where name contains UPDATED_NAME
        defaultDistrictShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where name does not contain DEFAULT_NAME
        defaultDistrictShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the districtList where name does not contain UPDATED_NAME
        defaultDistrictShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdAt equals to DEFAULT_CREATED_AT
        defaultDistrictShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the districtList where createdAt equals to UPDATED_CREATED_AT
        defaultDistrictShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultDistrictShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the districtList where createdAt equals to UPDATED_CREATED_AT
        defaultDistrictShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdAt is not null
        defaultDistrictShouldBeFound("createdAt.specified=true");

        // Get all the districtList where createdAt is null
        defaultDistrictShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdBy equals to DEFAULT_CREATED_BY
        defaultDistrictShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the districtList where createdBy equals to UPDATED_CREATED_BY
        defaultDistrictShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultDistrictShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the districtList where createdBy equals to UPDATED_CREATED_BY
        defaultDistrictShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdBy is not null
        defaultDistrictShouldBeFound("createdBy.specified=true");

        // Get all the districtList where createdBy is null
        defaultDistrictShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdBy contains DEFAULT_CREATED_BY
        defaultDistrictShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the districtList where createdBy contains UPDATED_CREATED_BY
        defaultDistrictShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where createdBy does not contain DEFAULT_CREATED_BY
        defaultDistrictShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the districtList where createdBy does not contain UPDATED_CREATED_BY
        defaultDistrictShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultDistrictShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the districtList where updatedAt equals to UPDATED_UPDATED_AT
        defaultDistrictShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultDistrictShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the districtList where updatedAt equals to UPDATED_UPDATED_AT
        defaultDistrictShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedAt is not null
        defaultDistrictShouldBeFound("updatedAt.specified=true");

        // Get all the districtList where updatedAt is null
        defaultDistrictShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultDistrictShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the districtList where updatedBy equals to UPDATED_UPDATED_BY
        defaultDistrictShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultDistrictShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the districtList where updatedBy equals to UPDATED_UPDATED_BY
        defaultDistrictShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedBy is not null
        defaultDistrictShouldBeFound("updatedBy.specified=true");

        // Get all the districtList where updatedBy is null
        defaultDistrictShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedBy contains DEFAULT_UPDATED_BY
        defaultDistrictShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the districtList where updatedBy contains UPDATED_UPDATED_BY
        defaultDistrictShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultDistrictShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the districtList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultDistrictShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where active equals to DEFAULT_ACTIVE
        defaultDistrictShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the districtList where active equals to UPDATED_ACTIVE
        defaultDistrictShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDistrictsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultDistrictShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the districtList where active equals to UPDATED_ACTIVE
        defaultDistrictShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllDistrictsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        // Get all the districtList where active is not null
        defaultDistrictShouldBeFound("active.specified=true");

        // Get all the districtList where active is null
        defaultDistrictShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByTalukaIsEqualToSomething() throws Exception {
        Taluka taluka;
        if (TestUtil.findAll(em, Taluka.class).isEmpty()) {
            districtRepository.saveAndFlush(district);
            taluka = TalukaResourceIT.createEntity(em);
        } else {
            taluka = TestUtil.findAll(em, Taluka.class).get(0);
        }
        em.persist(taluka);
        em.flush();
        district.addTaluka(taluka);
        districtRepository.saveAndFlush(district);
        Long talukaId = taluka.getId();
        // Get all the districtList where taluka equals to talukaId
        defaultDistrictShouldBeFound("talukaId.equals=" + talukaId);

        // Get all the districtList where taluka equals to (talukaId + 1)
        defaultDistrictShouldNotBeFound("talukaId.equals=" + (talukaId + 1));
    }

    @Test
    @Transactional
    void getAllDistrictsByUserLocationIsEqualToSomething() throws Exception {
        UserLocation userLocation;
        if (TestUtil.findAll(em, UserLocation.class).isEmpty()) {
            districtRepository.saveAndFlush(district);
            userLocation = UserLocationResourceIT.createEntity(em);
        } else {
            userLocation = TestUtil.findAll(em, UserLocation.class).get(0);
        }
        em.persist(userLocation);
        em.flush();
        district.addUserLocation(userLocation);
        districtRepository.saveAndFlush(district);
        Long userLocationId = userLocation.getId();
        // Get all the districtList where userLocation equals to userLocationId
        defaultDistrictShouldBeFound("userLocationId.equals=" + userLocationId);

        // Get all the districtList where userLocation equals to (userLocationId + 1)
        defaultDistrictShouldNotBeFound("userLocationId.equals=" + (userLocationId + 1));
    }

    @Test
    @Transactional
    void getAllDistrictsByStateIsEqualToSomething() throws Exception {
        State state;
        if (TestUtil.findAll(em, State.class).isEmpty()) {
            districtRepository.saveAndFlush(district);
            state = StateResourceIT.createEntity(em);
        } else {
            state = TestUtil.findAll(em, State.class).get(0);
        }
        em.persist(state);
        em.flush();
        district.setState(state);
        districtRepository.saveAndFlush(district);
        Long stateId = state.getId();
        // Get all the districtList where state equals to stateId
        defaultDistrictShouldBeFound("stateId.equals=" + stateId);

        // Get all the districtList where state equals to (stateId + 1)
        defaultDistrictShouldNotBeFound("stateId.equals=" + (stateId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDistrictShouldBeFound(String filter) throws Exception {
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(district.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDistrictShouldNotBeFound(String filter) throws Exception {
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDistrict() throws Exception {
        // Get the district
        restDistrictMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        int databaseSizeBeforeUpdate = districtRepository.findAll().size();

        // Update the district
        District updatedDistrict = districtRepository.findById(district.getId()).get();
        // Disconnect from session so that the updates on updatedDistrict are not directly saved in db
        em.detach(updatedDistrict);
        updatedDistrict
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        DistrictDTO districtDTO = districtMapper.toDto(updatedDistrict);

        restDistrictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, districtDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(districtDTO))
            )
            .andExpect(status().isOk());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDistrict.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testDistrict.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDistrict.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testDistrict.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testDistrict.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();
        district.setId(count.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, districtDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();
        district.setId(count.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();
        district.setId(count.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(districtDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        int databaseSizeBeforeUpdate = districtRepository.findAll().size();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.name(UPDATED_NAME).updatedBy(UPDATED_UPDATED_BY);

        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDistrict))
            )
            .andExpect(status().isOk());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDistrict.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testDistrict.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDistrict.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testDistrict.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testDistrict.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        int databaseSizeBeforeUpdate = districtRepository.findAll().size();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDistrict))
            )
            .andExpect(status().isOk());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDistrict.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testDistrict.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDistrict.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testDistrict.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testDistrict.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();
        district.setId(count.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, districtDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();
        district.setId(count.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().size();
        district.setId(count.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(districtDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDistrict() throws Exception {
        // Initialize the database
        districtRepository.saveAndFlush(district);

        int databaseSizeBeforeDelete = districtRepository.findAll().size();

        // Delete the district
        restDistrictMockMvc
            .perform(delete(ENTITY_API_URL_ID, district.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<District> districtList = districtRepository.findAll();
        assertThat(districtList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
