package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Country;
import com.mgs.aan.domain.District;
import com.mgs.aan.domain.State;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.repository.StateRepository;
import com.mgs.aan.service.criteria.StateCriteria;
import com.mgs.aan.service.dto.StateDTO;
import com.mgs.aan.service.mapper.StateMapper;
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
 * Integration tests for the {@link StateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StateResourceIT {

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

    private static final String ENTITY_API_URL = "/api/states";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStateMockMvc;

    private State state;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static State createEntity(EntityManager em) {
        State state = new State()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return state;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static State createUpdatedEntity(EntityManager em) {
        State state = new State()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return state;
    }

    @BeforeEach
    public void initTest() {
        state = createEntity(em);
    }

    @Test
    @Transactional
    void createState() throws Exception {
        int databaseSizeBeforeCreate = stateRepository.findAll().size();
        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);
        restStateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stateDTO)))
            .andExpect(status().isCreated());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeCreate + 1);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testState.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testState.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testState.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testState.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testState.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createStateWithExistingId() throws Exception {
        // Create the State with an existing ID
        state.setId(1L);
        StateDTO stateDTO = stateMapper.toDto(state);

        int databaseSizeBeforeCreate = stateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = stateRepository.findAll().size();
        // set the field null
        state.setActive(null);

        // Create the State, which fails.
        StateDTO stateDTO = stateMapper.toDto(state);

        restStateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stateDTO)))
            .andExpect(status().isBadRequest());

        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStates() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList
        restStateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(state.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getState() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get the state
        restStateMockMvc
            .perform(get(ENTITY_API_URL_ID, state.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(state.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getStatesByIdFiltering() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        Long id = state.getId();

        defaultStateShouldBeFound("id.equals=" + id);
        defaultStateShouldNotBeFound("id.notEquals=" + id);

        defaultStateShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStateShouldNotBeFound("id.greaterThan=" + id);

        defaultStateShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStateShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStatesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where name equals to DEFAULT_NAME
        defaultStateShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the stateList where name equals to UPDATED_NAME
        defaultStateShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where name in DEFAULT_NAME or UPDATED_NAME
        defaultStateShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the stateList where name equals to UPDATED_NAME
        defaultStateShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where name is not null
        defaultStateShouldBeFound("name.specified=true");

        // Get all the stateList where name is null
        defaultStateShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllStatesByNameContainsSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where name contains DEFAULT_NAME
        defaultStateShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the stateList where name contains UPDATED_NAME
        defaultStateShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where name does not contain DEFAULT_NAME
        defaultStateShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the stateList where name does not contain UPDATED_NAME
        defaultStateShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdAt equals to DEFAULT_CREATED_AT
        defaultStateShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the stateList where createdAt equals to UPDATED_CREATED_AT
        defaultStateShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStatesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultStateShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the stateList where createdAt equals to UPDATED_CREATED_AT
        defaultStateShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStatesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdAt is not null
        defaultStateShouldBeFound("createdAt.specified=true");

        // Get all the stateList where createdAt is null
        defaultStateShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStatesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdBy equals to DEFAULT_CREATED_BY
        defaultStateShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the stateList where createdBy equals to UPDATED_CREATED_BY
        defaultStateShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultStateShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the stateList where createdBy equals to UPDATED_CREATED_BY
        defaultStateShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdBy is not null
        defaultStateShouldBeFound("createdBy.specified=true");

        // Get all the stateList where createdBy is null
        defaultStateShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllStatesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdBy contains DEFAULT_CREATED_BY
        defaultStateShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the stateList where createdBy contains UPDATED_CREATED_BY
        defaultStateShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where createdBy does not contain DEFAULT_CREATED_BY
        defaultStateShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the stateList where createdBy does not contain UPDATED_CREATED_BY
        defaultStateShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultStateShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the stateList where updatedAt equals to UPDATED_UPDATED_AT
        defaultStateShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultStateShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the stateList where updatedAt equals to UPDATED_UPDATED_AT
        defaultStateShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedAt is not null
        defaultStateShouldBeFound("updatedAt.specified=true");

        // Get all the stateList where updatedAt is null
        defaultStateShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultStateShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the stateList where updatedBy equals to UPDATED_UPDATED_BY
        defaultStateShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultStateShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the stateList where updatedBy equals to UPDATED_UPDATED_BY
        defaultStateShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedBy is not null
        defaultStateShouldBeFound("updatedBy.specified=true");

        // Get all the stateList where updatedBy is null
        defaultStateShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedBy contains DEFAULT_UPDATED_BY
        defaultStateShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the stateList where updatedBy contains UPDATED_UPDATED_BY
        defaultStateShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultStateShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the stateList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultStateShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllStatesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where active equals to DEFAULT_ACTIVE
        defaultStateShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the stateList where active equals to UPDATED_ACTIVE
        defaultStateShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStatesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultStateShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the stateList where active equals to UPDATED_ACTIVE
        defaultStateShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStatesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        // Get all the stateList where active is not null
        defaultStateShouldBeFound("active.specified=true");

        // Get all the stateList where active is null
        defaultStateShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllStatesByDistrictIsEqualToSomething() throws Exception {
        District district;
        if (TestUtil.findAll(em, District.class).isEmpty()) {
            stateRepository.saveAndFlush(state);
            district = DistrictResourceIT.createEntity(em);
        } else {
            district = TestUtil.findAll(em, District.class).get(0);
        }
        em.persist(district);
        em.flush();
        state.addDistrict(district);
        stateRepository.saveAndFlush(state);
        Long districtId = district.getId();
        // Get all the stateList where district equals to districtId
        defaultStateShouldBeFound("districtId.equals=" + districtId);

        // Get all the stateList where district equals to (districtId + 1)
        defaultStateShouldNotBeFound("districtId.equals=" + (districtId + 1));
    }

    @Test
    @Transactional
    void getAllStatesByUserLocationIsEqualToSomething() throws Exception {
        UserLocation userLocation;
        if (TestUtil.findAll(em, UserLocation.class).isEmpty()) {
            stateRepository.saveAndFlush(state);
            userLocation = UserLocationResourceIT.createEntity(em);
        } else {
            userLocation = TestUtil.findAll(em, UserLocation.class).get(0);
        }
        em.persist(userLocation);
        em.flush();
        state.addUserLocation(userLocation);
        stateRepository.saveAndFlush(state);
        Long userLocationId = userLocation.getId();
        // Get all the stateList where userLocation equals to userLocationId
        defaultStateShouldBeFound("userLocationId.equals=" + userLocationId);

        // Get all the stateList where userLocation equals to (userLocationId + 1)
        defaultStateShouldNotBeFound("userLocationId.equals=" + (userLocationId + 1));
    }

    @Test
    @Transactional
    void getAllStatesByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            stateRepository.saveAndFlush(state);
            country = CountryResourceIT.createEntity(em);
        } else {
            country = TestUtil.findAll(em, Country.class).get(0);
        }
        em.persist(country);
        em.flush();
        state.setCountry(country);
        stateRepository.saveAndFlush(state);
        Long countryId = country.getId();
        // Get all the stateList where country equals to countryId
        defaultStateShouldBeFound("countryId.equals=" + countryId);

        // Get all the stateList where country equals to (countryId + 1)
        defaultStateShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStateShouldBeFound(String filter) throws Exception {
        restStateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(state.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restStateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStateShouldNotBeFound(String filter) throws Exception {
        restStateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingState() throws Exception {
        // Get the state
        restStateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingState() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        int databaseSizeBeforeUpdate = stateRepository.findAll().size();

        // Update the state
        State updatedState = stateRepository.findById(state.getId()).get();
        // Disconnect from session so that the updates on updatedState are not directly saved in db
        em.detach(updatedState);
        updatedState
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        StateDTO stateDTO = stateMapper.toDto(updatedState);

        restStateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stateDTO))
            )
            .andExpect(status().isOk());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testState.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testState.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testState.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testState.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testState.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().size();
        state.setId(count.incrementAndGet());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().size();
        state.setId(count.incrementAndGet());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().size();
        state.setId(count.incrementAndGet());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStateWithPatch() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        int databaseSizeBeforeUpdate = stateRepository.findAll().size();

        // Update the state using partial update
        State partialUpdatedState = new State();
        partialUpdatedState.setId(state.getId());

        partialUpdatedState.name(UPDATED_NAME).createdBy(UPDATED_CREATED_BY).updatedBy(UPDATED_UPDATED_BY);

        restStateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedState.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedState))
            )
            .andExpect(status().isOk());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testState.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testState.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testState.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testState.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testState.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateStateWithPatch() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        int databaseSizeBeforeUpdate = stateRepository.findAll().size();

        // Update the state using partial update
        State partialUpdatedState = new State();
        partialUpdatedState.setId(state.getId());

        partialUpdatedState
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restStateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedState.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedState))
            )
            .andExpect(status().isOk());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testState.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testState.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testState.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testState.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testState.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().size();
        state.setId(count.incrementAndGet());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().size();
        state.setId(count.incrementAndGet());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().size();
        state.setId(count.incrementAndGet());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteState() throws Exception {
        // Initialize the database
        stateRepository.saveAndFlush(state);

        int databaseSizeBeforeDelete = stateRepository.findAll().size();

        // Delete the state
        restStateMockMvc
            .perform(delete(ENTITY_API_URL_ID, state.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<State> stateList = stateRepository.findAll();
        assertThat(stateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
