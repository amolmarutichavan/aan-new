package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Country;
import com.mgs.aan.domain.State;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.repository.CountryRepository;
import com.mgs.aan.service.criteria.CountryCriteria;
import com.mgs.aan.service.dto.CountryDTO;
import com.mgs.aan.service.mapper.CountryMapper;
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
 * Integration tests for the {@link CountryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CountryResourceIT {

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

    private static final String ENTITY_API_URL = "/api/countries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCountryMockMvc;

    private Country country;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createEntity(EntityManager em) {
        Country country = new Country()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return country;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createUpdatedEntity(EntityManager em) {
        Country country = new Country()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return country;
    }

    @BeforeEach
    public void initTest() {
        country = createEntity(em);
    }

    @Test
    @Transactional
    void createCountry() throws Exception {
        int databaseSizeBeforeCreate = countryRepository.findAll().size();
        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);
        restCountryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isCreated());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeCreate + 1);
        Country testCountry = countryList.get(countryList.size() - 1);
        assertThat(testCountry.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCountry.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCountry.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCountry.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testCountry.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testCountry.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createCountryWithExistingId() throws Exception {
        // Create the Country with an existing ID
        country.setId(1L);
        CountryDTO countryDTO = countryMapper.toDto(country);

        int databaseSizeBeforeCreate = countryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCountryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = countryRepository.findAll().size();
        // set the field null
        country.setActive(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        restCountryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCountries() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList
        restCountryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getCountry() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get the country
        restCountryMockMvc
            .perform(get(ENTITY_API_URL_ID, country.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(country.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getCountriesByIdFiltering() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        Long id = country.getId();

        defaultCountryShouldBeFound("id.equals=" + id);
        defaultCountryShouldNotBeFound("id.notEquals=" + id);

        defaultCountryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCountryShouldNotBeFound("id.greaterThan=" + id);

        defaultCountryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCountryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCountriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where name equals to DEFAULT_NAME
        defaultCountryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the countryList where name equals to UPDATED_NAME
        defaultCountryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCountriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCountryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the countryList where name equals to UPDATED_NAME
        defaultCountryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCountriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where name is not null
        defaultCountryShouldBeFound("name.specified=true");

        // Get all the countryList where name is null
        defaultCountryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCountriesByNameContainsSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where name contains DEFAULT_NAME
        defaultCountryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the countryList where name contains UPDATED_NAME
        defaultCountryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCountriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where name does not contain DEFAULT_NAME
        defaultCountryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the countryList where name does not contain UPDATED_NAME
        defaultCountryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdAt equals to DEFAULT_CREATED_AT
        defaultCountryShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the countryList where createdAt equals to UPDATED_CREATED_AT
        defaultCountryShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultCountryShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the countryList where createdAt equals to UPDATED_CREATED_AT
        defaultCountryShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdAt is not null
        defaultCountryShouldBeFound("createdAt.specified=true");

        // Get all the countryList where createdAt is null
        defaultCountryShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdBy equals to DEFAULT_CREATED_BY
        defaultCountryShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the countryList where createdBy equals to UPDATED_CREATED_BY
        defaultCountryShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultCountryShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the countryList where createdBy equals to UPDATED_CREATED_BY
        defaultCountryShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdBy is not null
        defaultCountryShouldBeFound("createdBy.specified=true");

        // Get all the countryList where createdBy is null
        defaultCountryShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdBy contains DEFAULT_CREATED_BY
        defaultCountryShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the countryList where createdBy contains UPDATED_CREATED_BY
        defaultCountryShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where createdBy does not contain DEFAULT_CREATED_BY
        defaultCountryShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the countryList where createdBy does not contain UPDATED_CREATED_BY
        defaultCountryShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultCountryShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the countryList where updatedAt equals to UPDATED_UPDATED_AT
        defaultCountryShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultCountryShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the countryList where updatedAt equals to UPDATED_UPDATED_AT
        defaultCountryShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedAt is not null
        defaultCountryShouldBeFound("updatedAt.specified=true");

        // Get all the countryList where updatedAt is null
        defaultCountryShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultCountryShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the countryList where updatedBy equals to UPDATED_UPDATED_BY
        defaultCountryShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultCountryShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the countryList where updatedBy equals to UPDATED_UPDATED_BY
        defaultCountryShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedBy is not null
        defaultCountryShouldBeFound("updatedBy.specified=true");

        // Get all the countryList where updatedBy is null
        defaultCountryShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedBy contains DEFAULT_UPDATED_BY
        defaultCountryShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the countryList where updatedBy contains UPDATED_UPDATED_BY
        defaultCountryShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultCountryShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the countryList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultCountryShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllCountriesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where active equals to DEFAULT_ACTIVE
        defaultCountryShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the countryList where active equals to UPDATED_ACTIVE
        defaultCountryShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCountriesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultCountryShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the countryList where active equals to UPDATED_ACTIVE
        defaultCountryShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllCountriesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList where active is not null
        defaultCountryShouldBeFound("active.specified=true");

        // Get all the countryList where active is null
        defaultCountryShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllCountriesByStatesIsEqualToSomething() throws Exception {
        State states;
        if (TestUtil.findAll(em, State.class).isEmpty()) {
            countryRepository.saveAndFlush(country);
            states = StateResourceIT.createEntity(em);
        } else {
            states = TestUtil.findAll(em, State.class).get(0);
        }
        em.persist(states);
        em.flush();
        country.addStates(states);
        countryRepository.saveAndFlush(country);
        Long statesId = states.getId();
        // Get all the countryList where states equals to statesId
        defaultCountryShouldBeFound("statesId.equals=" + statesId);

        // Get all the countryList where states equals to (statesId + 1)
        defaultCountryShouldNotBeFound("statesId.equals=" + (statesId + 1));
    }

    @Test
    @Transactional
    void getAllCountriesByUserLocationIsEqualToSomething() throws Exception {
        UserLocation userLocation;
        if (TestUtil.findAll(em, UserLocation.class).isEmpty()) {
            countryRepository.saveAndFlush(country);
            userLocation = UserLocationResourceIT.createEntity(em);
        } else {
            userLocation = TestUtil.findAll(em, UserLocation.class).get(0);
        }
        em.persist(userLocation);
        em.flush();
        country.addUserLocation(userLocation);
        countryRepository.saveAndFlush(country);
        Long userLocationId = userLocation.getId();
        // Get all the countryList where userLocation equals to userLocationId
        defaultCountryShouldBeFound("userLocationId.equals=" + userLocationId);

        // Get all the countryList where userLocation equals to (userLocationId + 1)
        defaultCountryShouldNotBeFound("userLocationId.equals=" + (userLocationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCountryShouldBeFound(String filter) throws Exception {
        restCountryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restCountryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCountryShouldNotBeFound(String filter) throws Exception {
        restCountryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCountryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCountry() throws Exception {
        // Get the country
        restCountryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCountry() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        int databaseSizeBeforeUpdate = countryRepository.findAll().size();

        // Update the country
        Country updatedCountry = countryRepository.findById(country.getId()).get();
        // Disconnect from session so that the updates on updatedCountry are not directly saved in db
        em.detach(updatedCountry);
        updatedCountry
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        CountryDTO countryDTO = countryMapper.toDto(updatedCountry);

        restCountryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, countryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(countryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
        Country testCountry = countryList.get(countryList.size() - 1);
        assertThat(testCountry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCountry.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCountry.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCountry.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testCountry.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testCountry.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();
        country.setId(count.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, countryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();
        country.setId(count.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();
        country.setId(count.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCountryWithPatch() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        int databaseSizeBeforeUpdate = countryRepository.findAll().size();

        // Update the country using partial update
        Country partialUpdatedCountry = new Country();
        partialUpdatedCountry.setId(country.getId());

        partialUpdatedCountry.name(UPDATED_NAME).createdBy(UPDATED_CREATED_BY);

        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCountry))
            )
            .andExpect(status().isOk());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
        Country testCountry = countryList.get(countryList.size() - 1);
        assertThat(testCountry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCountry.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCountry.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCountry.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testCountry.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testCountry.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateCountryWithPatch() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        int databaseSizeBeforeUpdate = countryRepository.findAll().size();

        // Update the country using partial update
        Country partialUpdatedCountry = new Country();
        partialUpdatedCountry.setId(country.getId());

        partialUpdatedCountry
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCountry))
            )
            .andExpect(status().isOk());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
        Country testCountry = countryList.get(countryList.size() - 1);
        assertThat(testCountry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCountry.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCountry.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCountry.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testCountry.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testCountry.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();
        country.setId(count.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, countryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();
        country.setId(count.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();
        country.setId(count.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(countryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCountry() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        int databaseSizeBeforeDelete = countryRepository.findAll().size();

        // Delete the country
        restCountryMockMvc
            .perform(delete(ENTITY_API_URL_ID, country.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
