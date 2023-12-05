package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.domain.Village;
import com.mgs.aan.repository.VillageRepository;
import com.mgs.aan.service.criteria.VillageCriteria;
import com.mgs.aan.service.dto.VillageDTO;
import com.mgs.aan.service.mapper.VillageMapper;
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
 * Integration tests for the {@link VillageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VillageResourceIT {

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

    private static final String ENTITY_API_URL = "/api/villages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private VillageMapper villageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVillageMockMvc;

    private Village village;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Village createEntity(EntityManager em) {
        Village village = new Village()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return village;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Village createUpdatedEntity(EntityManager em) {
        Village village = new Village()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return village;
    }

    @BeforeEach
    public void initTest() {
        village = createEntity(em);
    }

    @Test
    @Transactional
    void createVillage() throws Exception {
        int databaseSizeBeforeCreate = villageRepository.findAll().size();
        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);
        restVillageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(villageDTO)))
            .andExpect(status().isCreated());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeCreate + 1);
        Village testVillage = villageList.get(villageList.size() - 1);
        assertThat(testVillage.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVillage.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testVillage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testVillage.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testVillage.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testVillage.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createVillageWithExistingId() throws Exception {
        // Create the Village with an existing ID
        village.setId(1L);
        VillageDTO villageDTO = villageMapper.toDto(village);

        int databaseSizeBeforeCreate = villageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVillageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(villageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = villageRepository.findAll().size();
        // set the field null
        village.setActive(null);

        // Create the Village, which fails.
        VillageDTO villageDTO = villageMapper.toDto(village);

        restVillageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(villageDTO)))
            .andExpect(status().isBadRequest());

        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVillages() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList
        restVillageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(village.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getVillage() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get the village
        restVillageMockMvc
            .perform(get(ENTITY_API_URL_ID, village.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(village.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getVillagesByIdFiltering() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        Long id = village.getId();

        defaultVillageShouldBeFound("id.equals=" + id);
        defaultVillageShouldNotBeFound("id.notEquals=" + id);

        defaultVillageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVillageShouldNotBeFound("id.greaterThan=" + id);

        defaultVillageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVillageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVillagesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where name equals to DEFAULT_NAME
        defaultVillageShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the villageList where name equals to UPDATED_NAME
        defaultVillageShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllVillagesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where name in DEFAULT_NAME or UPDATED_NAME
        defaultVillageShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the villageList where name equals to UPDATED_NAME
        defaultVillageShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllVillagesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where name is not null
        defaultVillageShouldBeFound("name.specified=true");

        // Get all the villageList where name is null
        defaultVillageShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllVillagesByNameContainsSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where name contains DEFAULT_NAME
        defaultVillageShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the villageList where name contains UPDATED_NAME
        defaultVillageShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllVillagesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where name does not contain DEFAULT_NAME
        defaultVillageShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the villageList where name does not contain UPDATED_NAME
        defaultVillageShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdAt equals to DEFAULT_CREATED_AT
        defaultVillageShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the villageList where createdAt equals to UPDATED_CREATED_AT
        defaultVillageShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultVillageShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the villageList where createdAt equals to UPDATED_CREATED_AT
        defaultVillageShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdAt is not null
        defaultVillageShouldBeFound("createdAt.specified=true");

        // Get all the villageList where createdAt is null
        defaultVillageShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdBy equals to DEFAULT_CREATED_BY
        defaultVillageShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the villageList where createdBy equals to UPDATED_CREATED_BY
        defaultVillageShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultVillageShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the villageList where createdBy equals to UPDATED_CREATED_BY
        defaultVillageShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdBy is not null
        defaultVillageShouldBeFound("createdBy.specified=true");

        // Get all the villageList where createdBy is null
        defaultVillageShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdBy contains DEFAULT_CREATED_BY
        defaultVillageShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the villageList where createdBy contains UPDATED_CREATED_BY
        defaultVillageShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where createdBy does not contain DEFAULT_CREATED_BY
        defaultVillageShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the villageList where createdBy does not contain UPDATED_CREATED_BY
        defaultVillageShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultVillageShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the villageList where updatedAt equals to UPDATED_UPDATED_AT
        defaultVillageShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultVillageShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the villageList where updatedAt equals to UPDATED_UPDATED_AT
        defaultVillageShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedAt is not null
        defaultVillageShouldBeFound("updatedAt.specified=true");

        // Get all the villageList where updatedAt is null
        defaultVillageShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultVillageShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the villageList where updatedBy equals to UPDATED_UPDATED_BY
        defaultVillageShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultVillageShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the villageList where updatedBy equals to UPDATED_UPDATED_BY
        defaultVillageShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedBy is not null
        defaultVillageShouldBeFound("updatedBy.specified=true");

        // Get all the villageList where updatedBy is null
        defaultVillageShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedBy contains DEFAULT_UPDATED_BY
        defaultVillageShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the villageList where updatedBy contains UPDATED_UPDATED_BY
        defaultVillageShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultVillageShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the villageList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultVillageShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllVillagesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where active equals to DEFAULT_ACTIVE
        defaultVillageShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the villageList where active equals to UPDATED_ACTIVE
        defaultVillageShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllVillagesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultVillageShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the villageList where active equals to UPDATED_ACTIVE
        defaultVillageShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllVillagesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        // Get all the villageList where active is not null
        defaultVillageShouldBeFound("active.specified=true");

        // Get all the villageList where active is null
        defaultVillageShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllVillagesByUserLocationIsEqualToSomething() throws Exception {
        UserLocation userLocation;
        if (TestUtil.findAll(em, UserLocation.class).isEmpty()) {
            villageRepository.saveAndFlush(village);
            userLocation = UserLocationResourceIT.createEntity(em);
        } else {
            userLocation = TestUtil.findAll(em, UserLocation.class).get(0);
        }
        em.persist(userLocation);
        em.flush();
        village.addUserLocation(userLocation);
        villageRepository.saveAndFlush(village);
        Long userLocationId = userLocation.getId();
        // Get all the villageList where userLocation equals to userLocationId
        defaultVillageShouldBeFound("userLocationId.equals=" + userLocationId);

        // Get all the villageList where userLocation equals to (userLocationId + 1)
        defaultVillageShouldNotBeFound("userLocationId.equals=" + (userLocationId + 1));
    }

    @Test
    @Transactional
    void getAllVillagesByTalukaIsEqualToSomething() throws Exception {
        Taluka taluka;
        if (TestUtil.findAll(em, Taluka.class).isEmpty()) {
            villageRepository.saveAndFlush(village);
            taluka = TalukaResourceIT.createEntity(em);
        } else {
            taluka = TestUtil.findAll(em, Taluka.class).get(0);
        }
        em.persist(taluka);
        em.flush();
        village.setTaluka(taluka);
        villageRepository.saveAndFlush(village);
        Long talukaId = taluka.getId();
        // Get all the villageList where taluka equals to talukaId
        defaultVillageShouldBeFound("talukaId.equals=" + talukaId);

        // Get all the villageList where taluka equals to (talukaId + 1)
        defaultVillageShouldNotBeFound("talukaId.equals=" + (talukaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVillageShouldBeFound(String filter) throws Exception {
        restVillageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(village.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restVillageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVillageShouldNotBeFound(String filter) throws Exception {
        restVillageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVillageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVillage() throws Exception {
        // Get the village
        restVillageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVillage() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        int databaseSizeBeforeUpdate = villageRepository.findAll().size();

        // Update the village
        Village updatedVillage = villageRepository.findById(village.getId()).get();
        // Disconnect from session so that the updates on updatedVillage are not directly saved in db
        em.detach(updatedVillage);
        updatedVillage
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        VillageDTO villageDTO = villageMapper.toDto(updatedVillage);

        restVillageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, villageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(villageDTO))
            )
            .andExpect(status().isOk());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
        Village testVillage = villageList.get(villageList.size() - 1);
        assertThat(testVillage.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVillage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testVillage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVillage.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testVillage.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testVillage.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingVillage() throws Exception {
        int databaseSizeBeforeUpdate = villageRepository.findAll().size();
        village.setId(count.incrementAndGet());

        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVillageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, villageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(villageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVillage() throws Exception {
        int databaseSizeBeforeUpdate = villageRepository.findAll().size();
        village.setId(count.incrementAndGet());

        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVillageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(villageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVillage() throws Exception {
        int databaseSizeBeforeUpdate = villageRepository.findAll().size();
        village.setId(count.incrementAndGet());

        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVillageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(villageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVillageWithPatch() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        int databaseSizeBeforeUpdate = villageRepository.findAll().size();

        // Update the village using partial update
        Village partialUpdatedVillage = new Village();
        partialUpdatedVillage.setId(village.getId());

        partialUpdatedVillage.createdBy(UPDATED_CREATED_BY).updatedAt(UPDATED_UPDATED_AT);

        restVillageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVillage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVillage))
            )
            .andExpect(status().isOk());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
        Village testVillage = villageList.get(villageList.size() - 1);
        assertThat(testVillage.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVillage.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testVillage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVillage.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testVillage.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testVillage.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateVillageWithPatch() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        int databaseSizeBeforeUpdate = villageRepository.findAll().size();

        // Update the village using partial update
        Village partialUpdatedVillage = new Village();
        partialUpdatedVillage.setId(village.getId());

        partialUpdatedVillage
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restVillageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVillage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVillage))
            )
            .andExpect(status().isOk());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
        Village testVillage = villageList.get(villageList.size() - 1);
        assertThat(testVillage.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVillage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testVillage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVillage.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testVillage.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testVillage.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingVillage() throws Exception {
        int databaseSizeBeforeUpdate = villageRepository.findAll().size();
        village.setId(count.incrementAndGet());

        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVillageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, villageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(villageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVillage() throws Exception {
        int databaseSizeBeforeUpdate = villageRepository.findAll().size();
        village.setId(count.incrementAndGet());

        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVillageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(villageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVillage() throws Exception {
        int databaseSizeBeforeUpdate = villageRepository.findAll().size();
        village.setId(count.incrementAndGet());

        // Create the Village
        VillageDTO villageDTO = villageMapper.toDto(village);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVillageMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(villageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Village in the database
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVillage() throws Exception {
        // Initialize the database
        villageRepository.saveAndFlush(village);

        int databaseSizeBeforeDelete = villageRepository.findAll().size();

        // Delete the village
        restVillageMockMvc
            .perform(delete(ENTITY_API_URL_ID, village.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Village> villageList = villageRepository.findAll();
        assertThat(villageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
