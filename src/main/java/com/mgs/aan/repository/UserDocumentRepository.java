package com.mgs.aan.repository;

import com.mgs.aan.domain.UserDocument;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Long>, JpaSpecificationExecutor<UserDocument> {
    @Query("select userDocument from UserDocument userDocument where userDocument.user.login = ?#{principal.username}")
    List<UserDocument> findByUserIsCurrentUser();
}
