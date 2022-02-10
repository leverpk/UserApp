package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.SimpleUser;
import com.mycompany.myapp.repository.SimpleUserRepository;
import com.mycompany.myapp.service.criteria.SimpleUserCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SimpleUser} entities in the database.
 * The main input is a {@link SimpleUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SimpleUser} or a {@link Page} of {@link SimpleUser} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SimpleUserQueryService extends QueryService<SimpleUser> {

    private final Logger log = LoggerFactory.getLogger(SimpleUserQueryService.class);

    private final SimpleUserRepository simpleUserRepository;

    public SimpleUserQueryService(SimpleUserRepository simpleUserRepository) {
        this.simpleUserRepository = simpleUserRepository;
    }

    /**
     * Return a {@link List} of {@link SimpleUser} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SimpleUser> findByCriteria(SimpleUserCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SimpleUser> specification = createSpecification(criteria);
        return simpleUserRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SimpleUser} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SimpleUser> findByCriteria(SimpleUserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SimpleUser> specification = createSpecification(criteria);
        return simpleUserRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SimpleUserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SimpleUser> specification = createSpecification(criteria);
        return simpleUserRepository.count(specification);
    }

    /**
     * Function to convert {@link SimpleUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SimpleUser> createSpecification(SimpleUserCriteria criteria) {
        Specification<SimpleUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SimpleUser_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), SimpleUser_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), SimpleUser_.lastName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), SimpleUser_.email));
            }
        }
        return specification;
    }
}
