package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SimpleUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SimpleUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SimpleUserRepository extends JpaRepository<SimpleUser, Long>, JpaSpecificationExecutor<SimpleUser> {}
