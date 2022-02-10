package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SimpleUser;
import com.mycompany.myapp.repository.SimpleUserRepository;
import com.mycompany.myapp.service.criteria.SimpleUserCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SimpleUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SimpleUserResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/simple-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SimpleUserRepository simpleUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSimpleUserMockMvc;

    private SimpleUser simpleUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SimpleUser createEntity(EntityManager em) {
        SimpleUser simpleUser = new SimpleUser().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME).email(DEFAULT_EMAIL);
        return simpleUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SimpleUser createUpdatedEntity(EntityManager em) {
        SimpleUser simpleUser = new SimpleUser().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL);
        return simpleUser;
    }

    @BeforeEach
    public void initTest() {
        simpleUser = createEntity(em);
    }

    @Test
    @Transactional
    void createSimpleUser() throws Exception {
        int databaseSizeBeforeCreate = simpleUserRepository.findAll().size();
        // Create the SimpleUser
        restSimpleUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(simpleUser)))
            .andExpect(status().isCreated());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeCreate + 1);
        SimpleUser testSimpleUser = simpleUserList.get(simpleUserList.size() - 1);
        assertThat(testSimpleUser.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testSimpleUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testSimpleUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void createSimpleUserWithExistingId() throws Exception {
        // Create the SimpleUser with an existing ID
        simpleUser.setId(1L);

        int databaseSizeBeforeCreate = simpleUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSimpleUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(simpleUser)))
            .andExpect(status().isBadRequest());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSimpleUsers() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList
        restSimpleUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(simpleUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getSimpleUser() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get the simpleUser
        restSimpleUserMockMvc
            .perform(get(ENTITY_API_URL_ID, simpleUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(simpleUser.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getSimpleUsersByIdFiltering() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        Long id = simpleUser.getId();

        defaultSimpleUserShouldBeFound("id.equals=" + id);
        defaultSimpleUserShouldNotBeFound("id.notEquals=" + id);

        defaultSimpleUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSimpleUserShouldNotBeFound("id.greaterThan=" + id);

        defaultSimpleUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSimpleUserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where firstName equals to DEFAULT_FIRST_NAME
        defaultSimpleUserShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the simpleUserList where firstName equals to UPDATED_FIRST_NAME
        defaultSimpleUserShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByFirstNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where firstName not equals to DEFAULT_FIRST_NAME
        defaultSimpleUserShouldNotBeFound("firstName.notEquals=" + DEFAULT_FIRST_NAME);

        // Get all the simpleUserList where firstName not equals to UPDATED_FIRST_NAME
        defaultSimpleUserShouldBeFound("firstName.notEquals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultSimpleUserShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the simpleUserList where firstName equals to UPDATED_FIRST_NAME
        defaultSimpleUserShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where firstName is not null
        defaultSimpleUserShouldBeFound("firstName.specified=true");

        // Get all the simpleUserList where firstName is null
        defaultSimpleUserShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllSimpleUsersByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where firstName contains DEFAULT_FIRST_NAME
        defaultSimpleUserShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the simpleUserList where firstName contains UPDATED_FIRST_NAME
        defaultSimpleUserShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where firstName does not contain DEFAULT_FIRST_NAME
        defaultSimpleUserShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the simpleUserList where firstName does not contain UPDATED_FIRST_NAME
        defaultSimpleUserShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where lastName equals to DEFAULT_LAST_NAME
        defaultSimpleUserShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the simpleUserList where lastName equals to UPDATED_LAST_NAME
        defaultSimpleUserShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByLastNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where lastName not equals to DEFAULT_LAST_NAME
        defaultSimpleUserShouldNotBeFound("lastName.notEquals=" + DEFAULT_LAST_NAME);

        // Get all the simpleUserList where lastName not equals to UPDATED_LAST_NAME
        defaultSimpleUserShouldBeFound("lastName.notEquals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultSimpleUserShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the simpleUserList where lastName equals to UPDATED_LAST_NAME
        defaultSimpleUserShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where lastName is not null
        defaultSimpleUserShouldBeFound("lastName.specified=true");

        // Get all the simpleUserList where lastName is null
        defaultSimpleUserShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllSimpleUsersByLastNameContainsSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where lastName contains DEFAULT_LAST_NAME
        defaultSimpleUserShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the simpleUserList where lastName contains UPDATED_LAST_NAME
        defaultSimpleUserShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where lastName does not contain DEFAULT_LAST_NAME
        defaultSimpleUserShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the simpleUserList where lastName does not contain UPDATED_LAST_NAME
        defaultSimpleUserShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where email equals to DEFAULT_EMAIL
        defaultSimpleUserShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the simpleUserList where email equals to UPDATED_EMAIL
        defaultSimpleUserShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where email not equals to DEFAULT_EMAIL
        defaultSimpleUserShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the simpleUserList where email not equals to UPDATED_EMAIL
        defaultSimpleUserShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultSimpleUserShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the simpleUserList where email equals to UPDATED_EMAIL
        defaultSimpleUserShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where email is not null
        defaultSimpleUserShouldBeFound("email.specified=true");

        // Get all the simpleUserList where email is null
        defaultSimpleUserShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllSimpleUsersByEmailContainsSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where email contains DEFAULT_EMAIL
        defaultSimpleUserShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the simpleUserList where email contains UPDATED_EMAIL
        defaultSimpleUserShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllSimpleUsersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        // Get all the simpleUserList where email does not contain DEFAULT_EMAIL
        defaultSimpleUserShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the simpleUserList where email does not contain UPDATED_EMAIL
        defaultSimpleUserShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSimpleUserShouldBeFound(String filter) throws Exception {
        restSimpleUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(simpleUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));

        // Check, that the count call also returns 1
        restSimpleUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSimpleUserShouldNotBeFound(String filter) throws Exception {
        restSimpleUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSimpleUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSimpleUser() throws Exception {
        // Get the simpleUser
        restSimpleUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSimpleUser() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();

        // Update the simpleUser
        SimpleUser updatedSimpleUser = simpleUserRepository.findById(simpleUser.getId()).get();
        // Disconnect from session so that the updates on updatedSimpleUser are not directly saved in db
        em.detach(updatedSimpleUser);
        updatedSimpleUser.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL);

        restSimpleUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSimpleUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSimpleUser))
            )
            .andExpect(status().isOk());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
        SimpleUser testSimpleUser = simpleUserList.get(simpleUserList.size() - 1);
        assertThat(testSimpleUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSimpleUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSimpleUser.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void putNonExistingSimpleUser() throws Exception {
        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();
        simpleUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSimpleUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, simpleUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(simpleUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSimpleUser() throws Exception {
        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();
        simpleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSimpleUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(simpleUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSimpleUser() throws Exception {
        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();
        simpleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSimpleUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(simpleUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSimpleUserWithPatch() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();

        // Update the simpleUser using partial update
        SimpleUser partialUpdatedSimpleUser = new SimpleUser();
        partialUpdatedSimpleUser.setId(simpleUser.getId());

        partialUpdatedSimpleUser.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL);

        restSimpleUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSimpleUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSimpleUser))
            )
            .andExpect(status().isOk());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
        SimpleUser testSimpleUser = simpleUserList.get(simpleUserList.size() - 1);
        assertThat(testSimpleUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSimpleUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSimpleUser.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateSimpleUserWithPatch() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();

        // Update the simpleUser using partial update
        SimpleUser partialUpdatedSimpleUser = new SimpleUser();
        partialUpdatedSimpleUser.setId(simpleUser.getId());

        partialUpdatedSimpleUser.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL);

        restSimpleUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSimpleUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSimpleUser))
            )
            .andExpect(status().isOk());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
        SimpleUser testSimpleUser = simpleUserList.get(simpleUserList.size() - 1);
        assertThat(testSimpleUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testSimpleUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSimpleUser.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingSimpleUser() throws Exception {
        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();
        simpleUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSimpleUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, simpleUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(simpleUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSimpleUser() throws Exception {
        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();
        simpleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSimpleUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(simpleUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSimpleUser() throws Exception {
        int databaseSizeBeforeUpdate = simpleUserRepository.findAll().size();
        simpleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSimpleUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(simpleUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SimpleUser in the database
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSimpleUser() throws Exception {
        // Initialize the database
        simpleUserRepository.saveAndFlush(simpleUser);

        int databaseSizeBeforeDelete = simpleUserRepository.findAll().size();

        // Delete the simpleUser
        restSimpleUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, simpleUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SimpleUser> simpleUserList = simpleUserRepository.findAll();
        assertThat(simpleUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
