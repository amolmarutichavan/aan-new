package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.Country;
import com.mgs.aan.domain.District;
import com.mgs.aan.domain.Pincode;
import com.mgs.aan.domain.State;
import com.mgs.aan.domain.Taluka;
import com.mgs.aan.domain.User;
import com.mgs.aan.domain.UserLocation;
import com.mgs.aan.domain.Village;
import com.mgs.aan.repository.UserLocationRepository;
import com.mgs.aan.service.UserLocationService;
import com.mgs.aan.service.criteria.UserLocationCriteria;
import com.mgs.aan.service.dto.UserLocationDTO;
import com.mgs.aan.service.mapper.UserLocationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link UserLocationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserLocationResourceIT {

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/user-locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Mock
    private UserLocationRepository userLocationRepositoryMock;

    @Autowired
    private UserLocationMapper userLocationMapper;

    @Mock
    private UserLocationService userLocationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserLocationMockMvc;

    private UserLocation userLocation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLocation createEntity(EntityManager em) {
        UserLocation userLocation = new UserLocation()
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return userLocation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLocation createUpdatedEntity(EntityManager em) {
        UserLocation userLocation = new UserLocation()
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return userLocation;
    }

    @BeforeEach
    public void initTest() {
        userLocation = createEntity(em);
    }

    @Test
    @Transactional
    void createUserLocation() throws Exception {
        int databaseSizeBeforeCreate = userLocationRepository.findAll().size();
        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);
        restUserLocationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeCreate + 1);
        UserLocation testUserLocation = userLocationList.get(userLocationList.size() - 1);
        assertThat(testUserLocation.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testUserLocation.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testUserLocation.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUserLocation.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testUserLocation.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserLocation.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testUserLocation.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createUserLocationWithExistingId() throws Exception {
        // Create the UserLocation with an existing ID
        userLocation.setId(1L);
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        int databaseSizeBeforeCreate = userLocationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserLocationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = userLocationRepository.findAll().size();
        // set the field null
        userLocation.setAddressLine1(null);

        // Create the UserLocation, which fails.
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        restUserLocationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = userLocationRepository.findAll().size();
        // set the field null
        userLocation.setActive(null);

        // Create the UserLocation, which fails.
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        restUserLocationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserLocations() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList
        restUserLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserLocationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userLocationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserLocationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userLocationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserLocationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userLocationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserLocationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userLocationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserLocation() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get the userLocation
        restUserLocationMockMvc
            .perform(get(ENTITY_API_URL_ID, userLocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userLocation.getId().intValue()))
            .andExpect(jsonPath("$.addressLine1").value(DEFAULT_ADDRESS_LINE_1))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getUserLocationsByIdFiltering() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        Long id = userLocation.getId();

        defaultUserLocationShouldBeFound("id.equals=" + id);
        defaultUserLocationShouldNotBeFound("id.notEquals=" + id);

        defaultUserLocationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserLocationShouldNotBeFound("id.greaterThan=" + id);

        defaultUserLocationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserLocationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine1IsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine1 equals to DEFAULT_ADDRESS_LINE_1
        defaultUserLocationShouldBeFound("addressLine1.equals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the userLocationList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultUserLocationShouldNotBeFound("addressLine1.equals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine1IsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine1 in DEFAULT_ADDRESS_LINE_1 or UPDATED_ADDRESS_LINE_1
        defaultUserLocationShouldBeFound("addressLine1.in=" + DEFAULT_ADDRESS_LINE_1 + "," + UPDATED_ADDRESS_LINE_1);

        // Get all the userLocationList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultUserLocationShouldNotBeFound("addressLine1.in=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine1IsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine1 is not null
        defaultUserLocationShouldBeFound("addressLine1.specified=true");

        // Get all the userLocationList where addressLine1 is null
        defaultUserLocationShouldNotBeFound("addressLine1.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine1ContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine1 contains DEFAULT_ADDRESS_LINE_1
        defaultUserLocationShouldBeFound("addressLine1.contains=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the userLocationList where addressLine1 contains UPDATED_ADDRESS_LINE_1
        defaultUserLocationShouldNotBeFound("addressLine1.contains=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine1NotContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine1 does not contain DEFAULT_ADDRESS_LINE_1
        defaultUserLocationShouldNotBeFound("addressLine1.doesNotContain=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the userLocationList where addressLine1 does not contain UPDATED_ADDRESS_LINE_1
        defaultUserLocationShouldBeFound("addressLine1.doesNotContain=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultUserLocationShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the userLocationList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultUserLocationShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultUserLocationShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the userLocationList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultUserLocationShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine2 is not null
        defaultUserLocationShouldBeFound("addressLine2.specified=true");

        // Get all the userLocationList where addressLine2 is null
        defaultUserLocationShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine2ContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine2 contains DEFAULT_ADDRESS_LINE_2
        defaultUserLocationShouldBeFound("addressLine2.contains=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the userLocationList where addressLine2 contains UPDATED_ADDRESS_LINE_2
        defaultUserLocationShouldNotBeFound("addressLine2.contains=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllUserLocationsByAddressLine2NotContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where addressLine2 does not contain DEFAULT_ADDRESS_LINE_2
        defaultUserLocationShouldNotBeFound("addressLine2.doesNotContain=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the userLocationList where addressLine2 does not contain UPDATED_ADDRESS_LINE_2
        defaultUserLocationShouldBeFound("addressLine2.doesNotContain=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdAt equals to DEFAULT_CREATED_AT
        defaultUserLocationShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the userLocationList where createdAt equals to UPDATED_CREATED_AT
        defaultUserLocationShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultUserLocationShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the userLocationList where createdAt equals to UPDATED_CREATED_AT
        defaultUserLocationShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdAt is not null
        defaultUserLocationShouldBeFound("createdAt.specified=true");

        // Get all the userLocationList where createdAt is null
        defaultUserLocationShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdBy equals to DEFAULT_CREATED_BY
        defaultUserLocationShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the userLocationList where createdBy equals to UPDATED_CREATED_BY
        defaultUserLocationShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultUserLocationShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the userLocationList where createdBy equals to UPDATED_CREATED_BY
        defaultUserLocationShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdBy is not null
        defaultUserLocationShouldBeFound("createdBy.specified=true");

        // Get all the userLocationList where createdBy is null
        defaultUserLocationShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdBy contains DEFAULT_CREATED_BY
        defaultUserLocationShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the userLocationList where createdBy contains UPDATED_CREATED_BY
        defaultUserLocationShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where createdBy does not contain DEFAULT_CREATED_BY
        defaultUserLocationShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the userLocationList where createdBy does not contain UPDATED_CREATED_BY
        defaultUserLocationShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultUserLocationShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the userLocationList where updatedAt equals to UPDATED_UPDATED_AT
        defaultUserLocationShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultUserLocationShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the userLocationList where updatedAt equals to UPDATED_UPDATED_AT
        defaultUserLocationShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedAt is not null
        defaultUserLocationShouldBeFound("updatedAt.specified=true");

        // Get all the userLocationList where updatedAt is null
        defaultUserLocationShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultUserLocationShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the userLocationList where updatedBy equals to UPDATED_UPDATED_BY
        defaultUserLocationShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultUserLocationShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the userLocationList where updatedBy equals to UPDATED_UPDATED_BY
        defaultUserLocationShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedBy is not null
        defaultUserLocationShouldBeFound("updatedBy.specified=true");

        // Get all the userLocationList where updatedBy is null
        defaultUserLocationShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedBy contains DEFAULT_UPDATED_BY
        defaultUserLocationShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the userLocationList where updatedBy contains UPDATED_UPDATED_BY
        defaultUserLocationShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultUserLocationShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the userLocationList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultUserLocationShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserLocationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where active equals to DEFAULT_ACTIVE
        defaultUserLocationShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the userLocationList where active equals to UPDATED_ACTIVE
        defaultUserLocationShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllUserLocationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultUserLocationShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the userLocationList where active equals to UPDATED_ACTIVE
        defaultUserLocationShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllUserLocationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        // Get all the userLocationList where active is not null
        defaultUserLocationShouldBeFound("active.specified=true");

        // Get all the userLocationList where active is null
        defaultUserLocationShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLocationsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        userLocation.setUser(user);
        userLocationRepository.saveAndFlush(userLocation);
        Long userId = user.getId();
        // Get all the userLocationList where user equals to userId
        defaultUserLocationShouldBeFound("userId.equals=" + userId);

        // Get all the userLocationList where user equals to (userId + 1)
        defaultUserLocationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllUserLocationsByPincodesIsEqualToSomething() throws Exception {
        Pincode pincodes;
        if (TestUtil.findAll(em, Pincode.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            pincodes = PincodeResourceIT.createEntity(em);
        } else {
            pincodes = TestUtil.findAll(em, Pincode.class).get(0);
        }
        em.persist(pincodes);
        em.flush();
        userLocation.addPincodes(pincodes);
        userLocationRepository.saveAndFlush(userLocation);
        Long pincodesId = pincodes.getId();
        // Get all the userLocationList where pincodes equals to pincodesId
        defaultUserLocationShouldBeFound("pincodesId.equals=" + pincodesId);

        // Get all the userLocationList where pincodes equals to (pincodesId + 1)
        defaultUserLocationShouldNotBeFound("pincodesId.equals=" + (pincodesId + 1));
    }

    @Test
    @Transactional
    void getAllUserLocationsByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            country = CountryResourceIT.createEntity(em);
        } else {
            country = TestUtil.findAll(em, Country.class).get(0);
        }
        em.persist(country);
        em.flush();
        userLocation.setCountry(country);
        userLocationRepository.saveAndFlush(userLocation);
        Long countryId = country.getId();
        // Get all the userLocationList where country equals to countryId
        defaultUserLocationShouldBeFound("countryId.equals=" + countryId);

        // Get all the userLocationList where country equals to (countryId + 1)
        defaultUserLocationShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    @Test
    @Transactional
    void getAllUserLocationsByStateIsEqualToSomething() throws Exception {
        State state;
        if (TestUtil.findAll(em, State.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            state = StateResourceIT.createEntity(em);
        } else {
            state = TestUtil.findAll(em, State.class).get(0);
        }
        em.persist(state);
        em.flush();
        userLocation.setState(state);
        userLocationRepository.saveAndFlush(userLocation);
        Long stateId = state.getId();
        // Get all the userLocationList where state equals to stateId
        defaultUserLocationShouldBeFound("stateId.equals=" + stateId);

        // Get all the userLocationList where state equals to (stateId + 1)
        defaultUserLocationShouldNotBeFound("stateId.equals=" + (stateId + 1));
    }

    @Test
    @Transactional
    void getAllUserLocationsByDistrictIsEqualToSomething() throws Exception {
        District district;
        if (TestUtil.findAll(em, District.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            district = DistrictResourceIT.createEntity(em);
        } else {
            district = TestUtil.findAll(em, District.class).get(0);
        }
        em.persist(district);
        em.flush();
        userLocation.setDistrict(district);
        userLocationRepository.saveAndFlush(userLocation);
        Long districtId = district.getId();
        // Get all the userLocationList where district equals to districtId
        defaultUserLocationShouldBeFound("districtId.equals=" + districtId);

        // Get all the userLocationList where district equals to (districtId + 1)
        defaultUserLocationShouldNotBeFound("districtId.equals=" + (districtId + 1));
    }

    @Test
    @Transactional
    void getAllUserLocationsByTalukaIsEqualToSomething() throws Exception {
        Taluka taluka;
        if (TestUtil.findAll(em, Taluka.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            taluka = TalukaResourceIT.createEntity(em);
        } else {
            taluka = TestUtil.findAll(em, Taluka.class).get(0);
        }
        em.persist(taluka);
        em.flush();
        userLocation.setTaluka(taluka);
        userLocationRepository.saveAndFlush(userLocation);
        Long talukaId = taluka.getId();
        // Get all the userLocationList where taluka equals to talukaId
        defaultUserLocationShouldBeFound("talukaId.equals=" + talukaId);

        // Get all the userLocationList where taluka equals to (talukaId + 1)
        defaultUserLocationShouldNotBeFound("talukaId.equals=" + (talukaId + 1));
    }

    @Test
    @Transactional
    void getAllUserLocationsByVillageIsEqualToSomething() throws Exception {
        Village village;
        if (TestUtil.findAll(em, Village.class).isEmpty()) {
            userLocationRepository.saveAndFlush(userLocation);
            village = VillageResourceIT.createEntity(em);
        } else {
            village = TestUtil.findAll(em, Village.class).get(0);
        }
        em.persist(village);
        em.flush();
        userLocation.setVillage(village);
        userLocationRepository.saveAndFlush(userLocation);
        Long villageId = village.getId();
        // Get all the userLocationList where village equals to villageId
        defaultUserLocationShouldBeFound("villageId.equals=" + villageId);

        // Get all the userLocationList where village equals to (villageId + 1)
        defaultUserLocationShouldNotBeFound("villageId.equals=" + (villageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserLocationShouldBeFound(String filter) throws Exception {
        restUserLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLocation.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restUserLocationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserLocationShouldNotBeFound(String filter) throws Exception {
        restUserLocationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserLocationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserLocation() throws Exception {
        // Get the userLocation
        restUserLocationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserLocation() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();

        // Update the userLocation
        UserLocation updatedUserLocation = userLocationRepository.findById(userLocation.getId()).get();
        // Disconnect from session so that the updates on updatedUserLocation are not directly saved in db
        em.detach(updatedUserLocation);
        updatedUserLocation
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(updatedUserLocation);

        restUserLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userLocationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
        UserLocation testUserLocation = userLocationList.get(userLocationList.size() - 1);
        assertThat(testUserLocation.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testUserLocation.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testUserLocation.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserLocation.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserLocation.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserLocation.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserLocation.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingUserLocation() throws Exception {
        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();
        userLocation.setId(count.incrementAndGet());

        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userLocationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserLocation() throws Exception {
        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();
        userLocation.setId(count.incrementAndGet());

        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLocationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserLocation() throws Exception {
        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();
        userLocation.setId(count.incrementAndGet());

        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLocationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserLocationWithPatch() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();

        // Update the userLocation using partial update
        UserLocation partialUpdatedUserLocation = new UserLocation();
        partialUpdatedUserLocation.setId(userLocation.getId());

        partialUpdatedUserLocation
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restUserLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserLocation))
            )
            .andExpect(status().isOk());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
        UserLocation testUserLocation = userLocationList.get(userLocationList.size() - 1);
        assertThat(testUserLocation.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testUserLocation.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testUserLocation.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUserLocation.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserLocation.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserLocation.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserLocation.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateUserLocationWithPatch() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();

        // Update the userLocation using partial update
        UserLocation partialUpdatedUserLocation = new UserLocation();
        partialUpdatedUserLocation.setId(userLocation.getId());

        partialUpdatedUserLocation
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restUserLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserLocation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserLocation))
            )
            .andExpect(status().isOk());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
        UserLocation testUserLocation = userLocationList.get(userLocationList.size() - 1);
        assertThat(testUserLocation.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testUserLocation.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testUserLocation.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserLocation.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserLocation.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserLocation.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserLocation.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingUserLocation() throws Exception {
        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();
        userLocation.setId(count.incrementAndGet());

        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userLocationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserLocation() throws Exception {
        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();
        userLocation.setId(count.incrementAndGet());

        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLocationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserLocation() throws Exception {
        int databaseSizeBeforeUpdate = userLocationRepository.findAll().size();
        userLocation.setId(count.incrementAndGet());

        // Create the UserLocation
        UserLocationDTO userLocationDTO = userLocationMapper.toDto(userLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLocationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userLocationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserLocation in the database
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserLocation() throws Exception {
        // Initialize the database
        userLocationRepository.saveAndFlush(userLocation);

        int databaseSizeBeforeDelete = userLocationRepository.findAll().size();

        // Delete the userLocation
        restUserLocationMockMvc
            .perform(delete(ENTITY_API_URL_ID, userLocation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserLocation> userLocationList = userLocationRepository.findAll();
        assertThat(userLocationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
