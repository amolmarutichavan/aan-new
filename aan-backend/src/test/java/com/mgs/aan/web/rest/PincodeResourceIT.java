package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Pincode;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.repository.PincodeRepository;
import com.mgs.aan.service.criteria.PincodeCriteria;
import com.mgs.aan.service.dto.PincodeDTO;
import com.mgs.aan.service.mapper.PincodeMapper;
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
 * Integration tests for the {@link PincodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PincodeResourceIT {

    private static final String DEFAULT_PIN_CODE = "AAAAAA";
    private static final String UPDATED_PIN_CODE = "BBBBBB";

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

    private static final String ENTITY_API_URL = "/api/pincodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private PincodeMapper pincodeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPincodeMockMvc;

    private Pincode pincode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pincode createEntity(EntityManager em) {
        Pincode pincode = new Pincode()
            .pinCode(DEFAULT_PIN_CODE)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return pincode;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pincode createUpdatedEntity(EntityManager em) {
        Pincode pincode = new Pincode()
            .pinCode(UPDATED_PIN_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return pincode;
    }

    @BeforeEach
    public void initTest() {
        pincode = createEntity(em);
    }

    @Test
    @Transactional
    void createPincode() throws Exception {
        int databaseSizeBeforeCreate = pincodeRepository.findAll().size();
        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);
        restPincodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pincodeDTO)))
            .andExpect(status().isCreated());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeCreate + 1);
        Pincode testPincode = pincodeList.get(pincodeList.size() - 1);
        assertThat(testPincode.getPinCode()).isEqualTo(DEFAULT_PIN_CODE);
        assertThat(testPincode.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPincode.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPincode.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testPincode.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPincode.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createPincodeWithExistingId() throws Exception {
        // Create the Pincode with an existing ID
        pincode.setId(1L);
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        int databaseSizeBeforeCreate = pincodeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPincodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pincodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPinCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pincodeRepository.findAll().size();
        // set the field null
        pincode.setPinCode(null);

        // Create the Pincode, which fails.
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        restPincodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pincodeDTO)))
            .andExpect(status().isBadRequest());

        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = pincodeRepository.findAll().size();
        // set the field null
        pincode.setActive(null);

        // Create the Pincode, which fails.
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        restPincodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pincodeDTO)))
            .andExpect(status().isBadRequest());

        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPincodes() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList
        restPincodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pincode.getId().intValue())))
            .andExpect(jsonPath("$.[*].pinCode").value(hasItem(DEFAULT_PIN_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getPincode() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get the pincode
        restPincodeMockMvc
            .perform(get(ENTITY_API_URL_ID, pincode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pincode.getId().intValue()))
            .andExpect(jsonPath("$.pinCode").value(DEFAULT_PIN_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getPincodesByIdFiltering() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        Long id = pincode.getId();

        defaultPincodeShouldBeFound("id.equals=" + id);
        defaultPincodeShouldNotBeFound("id.notEquals=" + id);

        defaultPincodeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPincodeShouldNotBeFound("id.greaterThan=" + id);

        defaultPincodeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPincodeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPincodesByPinCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where pinCode equals to DEFAULT_PIN_CODE
        defaultPincodeShouldBeFound("pinCode.equals=" + DEFAULT_PIN_CODE);

        // Get all the pincodeList where pinCode equals to UPDATED_PIN_CODE
        defaultPincodeShouldNotBeFound("pinCode.equals=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllPincodesByPinCodeIsInShouldWork() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where pinCode in DEFAULT_PIN_CODE or UPDATED_PIN_CODE
        defaultPincodeShouldBeFound("pinCode.in=" + DEFAULT_PIN_CODE + "," + UPDATED_PIN_CODE);

        // Get all the pincodeList where pinCode equals to UPDATED_PIN_CODE
        defaultPincodeShouldNotBeFound("pinCode.in=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllPincodesByPinCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where pinCode is not null
        defaultPincodeShouldBeFound("pinCode.specified=true");

        // Get all the pincodeList where pinCode is null
        defaultPincodeShouldNotBeFound("pinCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPincodesByPinCodeContainsSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where pinCode contains DEFAULT_PIN_CODE
        defaultPincodeShouldBeFound("pinCode.contains=" + DEFAULT_PIN_CODE);

        // Get all the pincodeList where pinCode contains UPDATED_PIN_CODE
        defaultPincodeShouldNotBeFound("pinCode.contains=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllPincodesByPinCodeNotContainsSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where pinCode does not contain DEFAULT_PIN_CODE
        defaultPincodeShouldNotBeFound("pinCode.doesNotContain=" + DEFAULT_PIN_CODE);

        // Get all the pincodeList where pinCode does not contain UPDATED_PIN_CODE
        defaultPincodeShouldBeFound("pinCode.doesNotContain=" + UPDATED_PIN_CODE);
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdAt equals to DEFAULT_CREATED_AT
        defaultPincodeShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the pincodeList where createdAt equals to UPDATED_CREATED_AT
        defaultPincodeShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultPincodeShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the pincodeList where createdAt equals to UPDATED_CREATED_AT
        defaultPincodeShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdAt is not null
        defaultPincodeShouldBeFound("createdAt.specified=true");

        // Get all the pincodeList where createdAt is null
        defaultPincodeShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdBy equals to DEFAULT_CREATED_BY
        defaultPincodeShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the pincodeList where createdBy equals to UPDATED_CREATED_BY
        defaultPincodeShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultPincodeShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the pincodeList where createdBy equals to UPDATED_CREATED_BY
        defaultPincodeShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdBy is not null
        defaultPincodeShouldBeFound("createdBy.specified=true");

        // Get all the pincodeList where createdBy is null
        defaultPincodeShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdBy contains DEFAULT_CREATED_BY
        defaultPincodeShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the pincodeList where createdBy contains UPDATED_CREATED_BY
        defaultPincodeShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where createdBy does not contain DEFAULT_CREATED_BY
        defaultPincodeShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the pincodeList where createdBy does not contain UPDATED_CREATED_BY
        defaultPincodeShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultPincodeShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the pincodeList where updatedAt equals to UPDATED_UPDATED_AT
        defaultPincodeShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultPincodeShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the pincodeList where updatedAt equals to UPDATED_UPDATED_AT
        defaultPincodeShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedAt is not null
        defaultPincodeShouldBeFound("updatedAt.specified=true");

        // Get all the pincodeList where updatedAt is null
        defaultPincodeShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultPincodeShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the pincodeList where updatedBy equals to UPDATED_UPDATED_BY
        defaultPincodeShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultPincodeShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the pincodeList where updatedBy equals to UPDATED_UPDATED_BY
        defaultPincodeShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedBy is not null
        defaultPincodeShouldBeFound("updatedBy.specified=true");

        // Get all the pincodeList where updatedBy is null
        defaultPincodeShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedBy contains DEFAULT_UPDATED_BY
        defaultPincodeShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the pincodeList where updatedBy contains UPDATED_UPDATED_BY
        defaultPincodeShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultPincodeShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the pincodeList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultPincodeShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllPincodesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where active equals to DEFAULT_ACTIVE
        defaultPincodeShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the pincodeList where active equals to UPDATED_ACTIVE
        defaultPincodeShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPincodesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultPincodeShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the pincodeList where active equals to UPDATED_ACTIVE
        defaultPincodeShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPincodesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        // Get all the pincodeList where active is not null
        defaultPincodeShouldBeFound("active.specified=true");

        // Get all the pincodeList where active is null
        defaultPincodeShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllPincodesByUserLocationIsEqualToSomething() throws Exception {
        UserLocation userLocation;
        if (TestUtil.findAll(em, UserLocation.class).isEmpty()) {
            pincodeRepository.saveAndFlush(pincode);
            userLocation = UserLocationResourceIT.createEntity(em);
        } else {
            userLocation = TestUtil.findAll(em, UserLocation.class).get(0);
        }
        em.persist(userLocation);
        em.flush();
        pincode.addUserLocation(userLocation);
        pincodeRepository.saveAndFlush(pincode);
        Long userLocationId = userLocation.getId();
        // Get all the pincodeList where userLocation equals to userLocationId
        defaultPincodeShouldBeFound("userLocationId.equals=" + userLocationId);

        // Get all the pincodeList where userLocation equals to (userLocationId + 1)
        defaultPincodeShouldNotBeFound("userLocationId.equals=" + (userLocationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPincodeShouldBeFound(String filter) throws Exception {
        restPincodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pincode.getId().intValue())))
            .andExpect(jsonPath("$.[*].pinCode").value(hasItem(DEFAULT_PIN_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restPincodeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPincodeShouldNotBeFound(String filter) throws Exception {
        restPincodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPincodeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPincode() throws Exception {
        // Get the pincode
        restPincodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPincode() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();

        // Update the pincode
        Pincode updatedPincode = pincodeRepository.findById(pincode.getId()).get();
        // Disconnect from session so that the updates on updatedPincode are not directly saved in db
        em.detach(updatedPincode);
        updatedPincode
            .pinCode(UPDATED_PIN_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        PincodeDTO pincodeDTO = pincodeMapper.toDto(updatedPincode);

        restPincodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pincodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pincodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
        Pincode testPincode = pincodeList.get(pincodeList.size() - 1);
        assertThat(testPincode.getPinCode()).isEqualTo(UPDATED_PIN_CODE);
        assertThat(testPincode.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPincode.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPincode.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPincode.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPincode.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingPincode() throws Exception {
        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();
        pincode.setId(count.incrementAndGet());

        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPincodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pincodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pincodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPincode() throws Exception {
        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();
        pincode.setId(count.incrementAndGet());

        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPincodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pincodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPincode() throws Exception {
        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();
        pincode.setId(count.incrementAndGet());

        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPincodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pincodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePincodeWithPatch() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();

        // Update the pincode using partial update
        Pincode partialUpdatedPincode = new Pincode();
        partialUpdatedPincode.setId(pincode.getId());

        partialUpdatedPincode.updatedAt(UPDATED_UPDATED_AT).active(UPDATED_ACTIVE);

        restPincodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPincode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPincode))
            )
            .andExpect(status().isOk());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
        Pincode testPincode = pincodeList.get(pincodeList.size() - 1);
        assertThat(testPincode.getPinCode()).isEqualTo(DEFAULT_PIN_CODE);
        assertThat(testPincode.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPincode.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPincode.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPincode.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPincode.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdatePincodeWithPatch() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();

        // Update the pincode using partial update
        Pincode partialUpdatedPincode = new Pincode();
        partialUpdatedPincode.setId(pincode.getId());

        partialUpdatedPincode
            .pinCode(UPDATED_PIN_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restPincodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPincode.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPincode))
            )
            .andExpect(status().isOk());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
        Pincode testPincode = pincodeList.get(pincodeList.size() - 1);
        assertThat(testPincode.getPinCode()).isEqualTo(UPDATED_PIN_CODE);
        assertThat(testPincode.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPincode.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPincode.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testPincode.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPincode.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingPincode() throws Exception {
        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();
        pincode.setId(count.incrementAndGet());

        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPincodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pincodeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pincodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPincode() throws Exception {
        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();
        pincode.setId(count.incrementAndGet());

        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPincodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pincodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPincode() throws Exception {
        int databaseSizeBeforeUpdate = pincodeRepository.findAll().size();
        pincode.setId(count.incrementAndGet());

        // Create the Pincode
        PincodeDTO pincodeDTO = pincodeMapper.toDto(pincode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPincodeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pincodeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pincode in the database
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePincode() throws Exception {
        // Initialize the database
        pincodeRepository.saveAndFlush(pincode);

        int databaseSizeBeforeDelete = pincodeRepository.findAll().size();

        // Delete the pincode
        restPincodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, pincode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pincode> pincodeList = pincodeRepository.findAll();
        assertThat(pincodeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
