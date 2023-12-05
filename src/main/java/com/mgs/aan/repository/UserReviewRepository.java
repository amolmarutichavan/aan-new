package com.mgs.aan.repository;

import com.mgs.aan.domain.UserReview;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long>, JpaSpecificationExecutor<UserReview> {
    @Query("select userReview from UserReview userReview where userReview.user.login = ?#{principal.username}")
    List<UserReview> findByUserIsCurrentUser();
}
