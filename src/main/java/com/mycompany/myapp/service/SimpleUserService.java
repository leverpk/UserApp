package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SimpleUser;
import com.mycompany.myapp.repository.SimpleUserRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SimpleUser}.
 */
@Service
@Transactional
public class SimpleUserService {

    private final Logger log = LoggerFactory.getLogger(SimpleUserService.class);

    private final SimpleUserRepository simpleUserRepository;

    public SimpleUserService(SimpleUserRepository simpleUserRepository) {
        this.simpleUserRepository = simpleUserRepository;
    }

    /**
     * Save a simpleUser.
     *
     * @param simpleUser the entity to save.
     * @return the persisted entity.
     */
    public SimpleUser save(SimpleUser simpleUser) {
        log.debug("Request to save SimpleUser : {}", simpleUser);
        return simpleUserRepository.save(simpleUser);
    }

    /**
     * Partially update a simpleUser.
     *
     * @param simpleUser the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SimpleUser> partialUpdate(SimpleUser simpleUser) {
        log.debug("Request to partially update SimpleUser : {}", simpleUser);

        return simpleUserRepository
            .findById(simpleUser.getId())
            .map(existingSimpleUser -> {
                if (simpleUser.getFirstName() != null) {
                    existingSimpleUser.setFirstName(simpleUser.getFirstName());
                }
                if (simpleUser.getLastName() != null) {
                    existingSimpleUser.setLastName(simpleUser.getLastName());
                }
                if (simpleUser.getEmail() != null) {
                    existingSimpleUser.setEmail(simpleUser.getEmail());
                }

                return existingSimpleUser;
            })
            .map(simpleUserRepository::save);
    }

    /**
     * Get all the simpleUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SimpleUser> findAll(Pageable pageable) {
        log.debug("Request to get all SimpleUsers");
        return simpleUserRepository.findAll(pageable);
    }

    /**
     * Get one simpleUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SimpleUser> findOne(Long id) {
        log.debug("Request to get SimpleUser : {}", id);
        return simpleUserRepository.findById(id);
    }

    /**
     * Delete the simpleUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SimpleUser : {}", id);
        simpleUserRepository.deleteById(id);
    }
}
