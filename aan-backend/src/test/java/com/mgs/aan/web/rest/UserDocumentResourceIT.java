package com.mgs.aan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mgs.aan.IntegrationTest;
import com.mgs.aan.domain.User;
import com.mgs.aan.domain.UserDocument;
import com.mgs.aan.repository.UserDocumentRepository;
import com.mgs.aan.service.criteria.UserDocumentCriteria;
import com.mgs.aan.service.dto.UserDocumentDTO;
import com.mgs.aan.service.mapper.UserDocumentMapper;
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
 * Integration tests for the {@link UserDocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserDocumentResourceIT {

    private static final String DEFAULT_DOCUMENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DOCUMENT_URL = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPLOAD_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOAD_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DOC_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_DOC_STATUS = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/user-documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private UserDocumentMapper userDocumentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDocumentMockMvc;

    private UserDocument userDocument;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserDocument createEntity(EntityManager em) {
        UserDocument userDocument = new UserDocument()
            .documentName(DEFAULT_DOCUMENT_NAME)
            .documentUrl(DEFAULT_DOCUMENT_URL)
            .uploadDateTime(DEFAULT_UPLOAD_DATE_TIME)
            .docStatus(DEFAULT_DOC_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY)
            .active(DEFAULT_ACTIVE);
        return userDocument;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserDocument createUpdatedEntity(EntityManager em) {
        UserDocument userDocument = new UserDocument()
            .documentName(UPDATED_DOCUMENT_NAME)
            .documentUrl(UPDATED_DOCUMENT_URL)
            .uploadDateTime(UPDATED_UPLOAD_DATE_TIME)
            .docStatus(UPDATED_DOC_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        return userDocument;
    }

    @BeforeEach
    public void initTest() {
        userDocument = createEntity(em);
    }

    @Test
    @Transactional
    void createUserDocument() throws Exception {
        int databaseSizeBeforeCreate = userDocumentRepository.findAll().size();
        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);
        restUserDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeCreate + 1);
        UserDocument testUserDocument = userDocumentList.get(userDocumentList.size() - 1);
        assertThat(testUserDocument.getDocumentName()).isEqualTo(DEFAULT_DOCUMENT_NAME);
        assertThat(testUserDocument.getDocumentUrl()).isEqualTo(DEFAULT_DOCUMENT_URL);
        assertThat(testUserDocument.getUploadDateTime()).isEqualTo(DEFAULT_UPLOAD_DATE_TIME);
        assertThat(testUserDocument.getDocStatus()).isEqualTo(DEFAULT_DOC_STATUS);
        assertThat(testUserDocument.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUserDocument.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testUserDocument.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserDocument.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testUserDocument.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createUserDocumentWithExistingId() throws Exception {
        // Create the UserDocument with an existing ID
        userDocument.setId(1L);
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        int databaseSizeBeforeCreate = userDocumentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDocumentNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDocumentRepository.findAll().size();
        // set the field null
        userDocument.setDocumentName(null);

        // Create the UserDocument, which fails.
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        restUserDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDocumentUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDocumentRepository.findAll().size();
        // set the field null
        userDocument.setDocumentUrl(null);

        // Create the UserDocument, which fails.
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        restUserDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDocStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDocumentRepository.findAll().size();
        // set the field null
        userDocument.setDocStatus(null);

        // Create the UserDocument, which fails.
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        restUserDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDocumentRepository.findAll().size();
        // set the field null
        userDocument.setActive(null);

        // Create the UserDocument, which fails.
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        restUserDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserDocuments() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList
        restUserDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].documentName").value(hasItem(DEFAULT_DOCUMENT_NAME)))
            .andExpect(jsonPath("$.[*].documentUrl").value(hasItem(DEFAULT_DOCUMENT_URL)))
            .andExpect(jsonPath("$.[*].uploadDateTime").value(hasItem(DEFAULT_UPLOAD_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].docStatus").value(hasItem(DEFAULT_DOC_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getUserDocument() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get the userDocument
        restUserDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, userDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userDocument.getId().intValue()))
            .andExpect(jsonPath("$.documentName").value(DEFAULT_DOCUMENT_NAME))
            .andExpect(jsonPath("$.documentUrl").value(DEFAULT_DOCUMENT_URL))
            .andExpect(jsonPath("$.uploadDateTime").value(DEFAULT_UPLOAD_DATE_TIME.toString()))
            .andExpect(jsonPath("$.docStatus").value(DEFAULT_DOC_STATUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getUserDocumentsByIdFiltering() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        Long id = userDocument.getId();

        defaultUserDocumentShouldBeFound("id.equals=" + id);
        defaultUserDocumentShouldNotBeFound("id.notEquals=" + id);

        defaultUserDocumentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserDocumentShouldNotBeFound("id.greaterThan=" + id);

        defaultUserDocumentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserDocumentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentNameIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentName equals to DEFAULT_DOCUMENT_NAME
        defaultUserDocumentShouldBeFound("documentName.equals=" + DEFAULT_DOCUMENT_NAME);

        // Get all the userDocumentList where documentName equals to UPDATED_DOCUMENT_NAME
        defaultUserDocumentShouldNotBeFound("documentName.equals=" + UPDATED_DOCUMENT_NAME);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentNameIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentName in DEFAULT_DOCUMENT_NAME or UPDATED_DOCUMENT_NAME
        defaultUserDocumentShouldBeFound("documentName.in=" + DEFAULT_DOCUMENT_NAME + "," + UPDATED_DOCUMENT_NAME);

        // Get all the userDocumentList where documentName equals to UPDATED_DOCUMENT_NAME
        defaultUserDocumentShouldNotBeFound("documentName.in=" + UPDATED_DOCUMENT_NAME);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentName is not null
        defaultUserDocumentShouldBeFound("documentName.specified=true");

        // Get all the userDocumentList where documentName is null
        defaultUserDocumentShouldNotBeFound("documentName.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentNameContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentName contains DEFAULT_DOCUMENT_NAME
        defaultUserDocumentShouldBeFound("documentName.contains=" + DEFAULT_DOCUMENT_NAME);

        // Get all the userDocumentList where documentName contains UPDATED_DOCUMENT_NAME
        defaultUserDocumentShouldNotBeFound("documentName.contains=" + UPDATED_DOCUMENT_NAME);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentNameNotContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentName does not contain DEFAULT_DOCUMENT_NAME
        defaultUserDocumentShouldNotBeFound("documentName.doesNotContain=" + DEFAULT_DOCUMENT_NAME);

        // Get all the userDocumentList where documentName does not contain UPDATED_DOCUMENT_NAME
        defaultUserDocumentShouldBeFound("documentName.doesNotContain=" + UPDATED_DOCUMENT_NAME);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentUrl equals to DEFAULT_DOCUMENT_URL
        defaultUserDocumentShouldBeFound("documentUrl.equals=" + DEFAULT_DOCUMENT_URL);

        // Get all the userDocumentList where documentUrl equals to UPDATED_DOCUMENT_URL
        defaultUserDocumentShouldNotBeFound("documentUrl.equals=" + UPDATED_DOCUMENT_URL);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentUrlIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentUrl in DEFAULT_DOCUMENT_URL or UPDATED_DOCUMENT_URL
        defaultUserDocumentShouldBeFound("documentUrl.in=" + DEFAULT_DOCUMENT_URL + "," + UPDATED_DOCUMENT_URL);

        // Get all the userDocumentList where documentUrl equals to UPDATED_DOCUMENT_URL
        defaultUserDocumentShouldNotBeFound("documentUrl.in=" + UPDATED_DOCUMENT_URL);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentUrl is not null
        defaultUserDocumentShouldBeFound("documentUrl.specified=true");

        // Get all the userDocumentList where documentUrl is null
        defaultUserDocumentShouldNotBeFound("documentUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentUrlContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentUrl contains DEFAULT_DOCUMENT_URL
        defaultUserDocumentShouldBeFound("documentUrl.contains=" + DEFAULT_DOCUMENT_URL);

        // Get all the userDocumentList where documentUrl contains UPDATED_DOCUMENT_URL
        defaultUserDocumentShouldNotBeFound("documentUrl.contains=" + UPDATED_DOCUMENT_URL);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocumentUrlNotContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where documentUrl does not contain DEFAULT_DOCUMENT_URL
        defaultUserDocumentShouldNotBeFound("documentUrl.doesNotContain=" + DEFAULT_DOCUMENT_URL);

        // Get all the userDocumentList where documentUrl does not contain UPDATED_DOCUMENT_URL
        defaultUserDocumentShouldBeFound("documentUrl.doesNotContain=" + UPDATED_DOCUMENT_URL);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUploadDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where uploadDateTime equals to DEFAULT_UPLOAD_DATE_TIME
        defaultUserDocumentShouldBeFound("uploadDateTime.equals=" + DEFAULT_UPLOAD_DATE_TIME);

        // Get all the userDocumentList where uploadDateTime equals to UPDATED_UPLOAD_DATE_TIME
        defaultUserDocumentShouldNotBeFound("uploadDateTime.equals=" + UPDATED_UPLOAD_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUploadDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where uploadDateTime in DEFAULT_UPLOAD_DATE_TIME or UPDATED_UPLOAD_DATE_TIME
        defaultUserDocumentShouldBeFound("uploadDateTime.in=" + DEFAULT_UPLOAD_DATE_TIME + "," + UPDATED_UPLOAD_DATE_TIME);

        // Get all the userDocumentList where uploadDateTime equals to UPDATED_UPLOAD_DATE_TIME
        defaultUserDocumentShouldNotBeFound("uploadDateTime.in=" + UPDATED_UPLOAD_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUploadDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where uploadDateTime is not null
        defaultUserDocumentShouldBeFound("uploadDateTime.specified=true");

        // Get all the userDocumentList where uploadDateTime is null
        defaultUserDocumentShouldNotBeFound("uploadDateTime.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where docStatus equals to DEFAULT_DOC_STATUS
        defaultUserDocumentShouldBeFound("docStatus.equals=" + DEFAULT_DOC_STATUS);

        // Get all the userDocumentList where docStatus equals to UPDATED_DOC_STATUS
        defaultUserDocumentShouldNotBeFound("docStatus.equals=" + UPDATED_DOC_STATUS);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocStatusIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where docStatus in DEFAULT_DOC_STATUS or UPDATED_DOC_STATUS
        defaultUserDocumentShouldBeFound("docStatus.in=" + DEFAULT_DOC_STATUS + "," + UPDATED_DOC_STATUS);

        // Get all the userDocumentList where docStatus equals to UPDATED_DOC_STATUS
        defaultUserDocumentShouldNotBeFound("docStatus.in=" + UPDATED_DOC_STATUS);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where docStatus is not null
        defaultUserDocumentShouldBeFound("docStatus.specified=true");

        // Get all the userDocumentList where docStatus is null
        defaultUserDocumentShouldNotBeFound("docStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocStatusContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where docStatus contains DEFAULT_DOC_STATUS
        defaultUserDocumentShouldBeFound("docStatus.contains=" + DEFAULT_DOC_STATUS);

        // Get all the userDocumentList where docStatus contains UPDATED_DOC_STATUS
        defaultUserDocumentShouldNotBeFound("docStatus.contains=" + UPDATED_DOC_STATUS);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByDocStatusNotContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where docStatus does not contain DEFAULT_DOC_STATUS
        defaultUserDocumentShouldNotBeFound("docStatus.doesNotContain=" + DEFAULT_DOC_STATUS);

        // Get all the userDocumentList where docStatus does not contain UPDATED_DOC_STATUS
        defaultUserDocumentShouldBeFound("docStatus.doesNotContain=" + UPDATED_DOC_STATUS);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdAt equals to DEFAULT_CREATED_AT
        defaultUserDocumentShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the userDocumentList where createdAt equals to UPDATED_CREATED_AT
        defaultUserDocumentShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultUserDocumentShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the userDocumentList where createdAt equals to UPDATED_CREATED_AT
        defaultUserDocumentShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdAt is not null
        defaultUserDocumentShouldBeFound("createdAt.specified=true");

        // Get all the userDocumentList where createdAt is null
        defaultUserDocumentShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdBy equals to DEFAULT_CREATED_BY
        defaultUserDocumentShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the userDocumentList where createdBy equals to UPDATED_CREATED_BY
        defaultUserDocumentShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultUserDocumentShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the userDocumentList where createdBy equals to UPDATED_CREATED_BY
        defaultUserDocumentShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdBy is not null
        defaultUserDocumentShouldBeFound("createdBy.specified=true");

        // Get all the userDocumentList where createdBy is null
        defaultUserDocumentShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdBy contains DEFAULT_CREATED_BY
        defaultUserDocumentShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the userDocumentList where createdBy contains UPDATED_CREATED_BY
        defaultUserDocumentShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where createdBy does not contain DEFAULT_CREATED_BY
        defaultUserDocumentShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the userDocumentList where createdBy does not contain UPDATED_CREATED_BY
        defaultUserDocumentShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultUserDocumentShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the userDocumentList where updatedAt equals to UPDATED_UPDATED_AT
        defaultUserDocumentShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultUserDocumentShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the userDocumentList where updatedAt equals to UPDATED_UPDATED_AT
        defaultUserDocumentShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedAt is not null
        defaultUserDocumentShouldBeFound("updatedAt.specified=true");

        // Get all the userDocumentList where updatedAt is null
        defaultUserDocumentShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultUserDocumentShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the userDocumentList where updatedBy equals to UPDATED_UPDATED_BY
        defaultUserDocumentShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultUserDocumentShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the userDocumentList where updatedBy equals to UPDATED_UPDATED_BY
        defaultUserDocumentShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedBy is not null
        defaultUserDocumentShouldBeFound("updatedBy.specified=true");

        // Get all the userDocumentList where updatedBy is null
        defaultUserDocumentShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedBy contains DEFAULT_UPDATED_BY
        defaultUserDocumentShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the userDocumentList where updatedBy contains UPDATED_UPDATED_BY
        defaultUserDocumentShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultUserDocumentShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the userDocumentList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultUserDocumentShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where active equals to DEFAULT_ACTIVE
        defaultUserDocumentShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the userDocumentList where active equals to UPDATED_ACTIVE
        defaultUserDocumentShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultUserDocumentShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the userDocumentList where active equals to UPDATED_ACTIVE
        defaultUserDocumentShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllUserDocumentsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        // Get all the userDocumentList where active is not null
        defaultUserDocumentShouldBeFound("active.specified=true");

        // Get all the userDocumentList where active is null
        defaultUserDocumentShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDocumentsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userDocumentRepository.saveAndFlush(userDocument);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        userDocument.setUser(user);
        userDocumentRepository.saveAndFlush(userDocument);
        Long userId = user.getId();
        // Get all the userDocumentList where user equals to userId
        defaultUserDocumentShouldBeFound("userId.equals=" + userId);

        // Get all the userDocumentList where user equals to (userId + 1)
        defaultUserDocumentShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserDocumentShouldBeFound(String filter) throws Exception {
        restUserDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].documentName").value(hasItem(DEFAULT_DOCUMENT_NAME)))
            .andExpect(jsonPath("$.[*].documentUrl").value(hasItem(DEFAULT_DOCUMENT_URL)))
            .andExpect(jsonPath("$.[*].uploadDateTime").value(hasItem(DEFAULT_UPLOAD_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].docStatus").value(hasItem(DEFAULT_DOC_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restUserDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserDocumentShouldNotBeFound(String filter) throws Exception {
        restUserDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserDocument() throws Exception {
        // Get the userDocument
        restUserDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserDocument() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();

        // Update the userDocument
        UserDocument updatedUserDocument = userDocumentRepository.findById(userDocument.getId()).get();
        // Disconnect from session so that the updates on updatedUserDocument are not directly saved in db
        em.detach(updatedUserDocument);
        updatedUserDocument
            .documentName(UPDATED_DOCUMENT_NAME)
            .documentUrl(UPDATED_DOCUMENT_URL)
            .uploadDateTime(UPDATED_UPLOAD_DATE_TIME)
            .docStatus(UPDATED_DOC_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(updatedUserDocument);

        restUserDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDocumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
        UserDocument testUserDocument = userDocumentList.get(userDocumentList.size() - 1);
        assertThat(testUserDocument.getDocumentName()).isEqualTo(UPDATED_DOCUMENT_NAME);
        assertThat(testUserDocument.getDocumentUrl()).isEqualTo(UPDATED_DOCUMENT_URL);
        assertThat(testUserDocument.getUploadDateTime()).isEqualTo(UPDATED_UPLOAD_DATE_TIME);
        assertThat(testUserDocument.getDocStatus()).isEqualTo(UPDATED_DOC_STATUS);
        assertThat(testUserDocument.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserDocument.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserDocument.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserDocument.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserDocument.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingUserDocument() throws Exception {
        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();
        userDocument.setId(count.incrementAndGet());

        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDocumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserDocument() throws Exception {
        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();
        userDocument.setId(count.incrementAndGet());

        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserDocument() throws Exception {
        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();
        userDocument.setId(count.incrementAndGet());

        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDocumentMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDocumentWithPatch() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();

        // Update the userDocument using partial update
        UserDocument partialUpdatedUserDocument = new UserDocument();
        partialUpdatedUserDocument.setId(userDocument.getId());

        partialUpdatedUserDocument
            .docStatus(UPDATED_DOC_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restUserDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserDocument))
            )
            .andExpect(status().isOk());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
        UserDocument testUserDocument = userDocumentList.get(userDocumentList.size() - 1);
        assertThat(testUserDocument.getDocumentName()).isEqualTo(DEFAULT_DOCUMENT_NAME);
        assertThat(testUserDocument.getDocumentUrl()).isEqualTo(DEFAULT_DOCUMENT_URL);
        assertThat(testUserDocument.getUploadDateTime()).isEqualTo(DEFAULT_UPLOAD_DATE_TIME);
        assertThat(testUserDocument.getDocStatus()).isEqualTo(UPDATED_DOC_STATUS);
        assertThat(testUserDocument.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUserDocument.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserDocument.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserDocument.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserDocument.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateUserDocumentWithPatch() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();

        // Update the userDocument using partial update
        UserDocument partialUpdatedUserDocument = new UserDocument();
        partialUpdatedUserDocument.setId(userDocument.getId());

        partialUpdatedUserDocument
            .documentName(UPDATED_DOCUMENT_NAME)
            .documentUrl(UPDATED_DOCUMENT_URL)
            .uploadDateTime(UPDATED_UPLOAD_DATE_TIME)
            .docStatus(UPDATED_DOC_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY)
            .active(UPDATED_ACTIVE);

        restUserDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserDocument))
            )
            .andExpect(status().isOk());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
        UserDocument testUserDocument = userDocumentList.get(userDocumentList.size() - 1);
        assertThat(testUserDocument.getDocumentName()).isEqualTo(UPDATED_DOCUMENT_NAME);
        assertThat(testUserDocument.getDocumentUrl()).isEqualTo(UPDATED_DOCUMENT_URL);
        assertThat(testUserDocument.getUploadDateTime()).isEqualTo(UPDATED_UPLOAD_DATE_TIME);
        assertThat(testUserDocument.getDocStatus()).isEqualTo(UPDATED_DOC_STATUS);
        assertThat(testUserDocument.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserDocument.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUserDocument.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserDocument.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testUserDocument.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingUserDocument() throws Exception {
        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();
        userDocument.setId(count.incrementAndGet());

        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDocumentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserDocument() throws Exception {
        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();
        userDocument.setId(count.incrementAndGet());

        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserDocument() throws Exception {
        int databaseSizeBeforeUpdate = userDocumentRepository.findAll().size();
        userDocument.setId(count.incrementAndGet());

        // Create the UserDocument
        UserDocumentDTO userDocumentDTO = userDocumentMapper.toDto(userDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userDocumentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserDocument in the database
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserDocument() throws Exception {
        // Initialize the database
        userDocumentRepository.saveAndFlush(userDocument);

        int databaseSizeBeforeDelete = userDocumentRepository.findAll().size();

        // Delete the userDocument
        restUserDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, userDocument.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserDocument> userDocumentList = userDocumentRepository.findAll();
        assertThat(userDocumentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
