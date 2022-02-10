package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.SimpleUser;
import com.mycompany.myapp.repository.SimpleUserRepository;
import com.mycompany.myapp.service.SimpleUserQueryService;
import com.mycompany.myapp.service.SimpleUserService;
import com.mycompany.myapp.service.criteria.SimpleUserCriteria;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.SimpleUser}.
 */
@RestController
@RequestMapping("/api")
public class SimpleUserResource {

    private final Logger log = LoggerFactory.getLogger(SimpleUserResource.class);

    private static final String ENTITY_NAME = "simpleUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SimpleUserService simpleUserService;

    private final SimpleUserRepository simpleUserRepository;

    private final SimpleUserQueryService simpleUserQueryService;

    public SimpleUserResource(
        SimpleUserService simpleUserService,
        SimpleUserRepository simpleUserRepository,
        SimpleUserQueryService simpleUserQueryService
    ) {
        this.simpleUserService = simpleUserService;
        this.simpleUserRepository = simpleUserRepository;
        this.simpleUserQueryService = simpleUserQueryService;
    }

    /**
     * {@code POST  /simple-users} : Create a new simpleUser.
     *
     * @param simpleUser the simpleUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new simpleUser, or with status {@code 400 (Bad Request)} if the simpleUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/simple-users")
    public ResponseEntity<SimpleUser> createSimpleUser(@RequestBody SimpleUser simpleUser) throws URISyntaxException {
        log.debug("REST request to save SimpleUser : {}", simpleUser);
        if (simpleUser.getId() != null) {
            throw new BadRequestAlertException("A new simpleUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SimpleUser result = simpleUserService.save(simpleUser);
        return ResponseEntity
            .created(new URI("/api/simple-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /simple-users/:id} : Updates an existing simpleUser.
     *
     * @param id the id of the simpleUser to save.
     * @param simpleUser the simpleUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated simpleUser,
     * or with status {@code 400 (Bad Request)} if the simpleUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the simpleUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/simple-users/{id}")
    public ResponseEntity<SimpleUser> updateSimpleUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SimpleUser simpleUser
    ) throws URISyntaxException {
        log.debug("REST request to update SimpleUser : {}, {}", id, simpleUser);
        if (simpleUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, simpleUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!simpleUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SimpleUser result = simpleUserService.save(simpleUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, simpleUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /simple-users/:id} : Partial updates given fields of an existing simpleUser, field will ignore if it is null
     *
     * @param id the id of the simpleUser to save.
     * @param simpleUser the simpleUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated simpleUser,
     * or with status {@code 400 (Bad Request)} if the simpleUser is not valid,
     * or with status {@code 404 (Not Found)} if the simpleUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the simpleUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/simple-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SimpleUser> partialUpdateSimpleUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SimpleUser simpleUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update SimpleUser partially : {}, {}", id, simpleUser);
        if (simpleUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, simpleUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!simpleUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SimpleUser> result = simpleUserService.partialUpdate(simpleUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, simpleUser.getId().toString())
        );
    }

    /**
     * {@code GET  /simple-users} : get all the simpleUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of simpleUsers in body.
     */
    @GetMapping("/simple-users")
    public ResponseEntity<List<SimpleUser>> getAllSimpleUsers(
        SimpleUserCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SimpleUsers by criteria: {}", criteria);
        Page<SimpleUser> page = simpleUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /simple-users/count} : count all the simpleUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/simple-users/count")
    public ResponseEntity<Long> countSimpleUsers(SimpleUserCriteria criteria) {
        log.debug("REST request to count SimpleUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(simpleUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /simple-users/:id} : get the "id" simpleUser.
     *
     * @param id the id of the simpleUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the simpleUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/simple-users/{id}")
    public ResponseEntity<SimpleUser> getSimpleUser(@PathVariable Long id) {
        log.debug("REST request to get SimpleUser : {}", id);
        Optional<SimpleUser> simpleUser = simpleUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(simpleUser);
    }

    /**
     * {@code DELETE  /simple-users/:id} : delete the "id" simpleUser.
     *
     * @param id the id of the simpleUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/simple-users/{id}")
    public ResponseEntity<Void> deleteSimpleUser(@PathVariable Long id) {
        log.debug("REST request to delete SimpleUser : {}", id);
        simpleUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
