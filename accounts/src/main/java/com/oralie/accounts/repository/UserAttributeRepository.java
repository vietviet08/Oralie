package com.oralie.accounts.repository;

import com.oralie.accounts.model.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, Long> {

    @Modifying
    @Query("DELETE FROM UserAttribute u WHERE u.account.userId = :userId")
    void deleteByUserId(String userId);

    @Query("SELECT u FROM UserAttribute u WHERE u.account.userId = :userId")
    Optional<UserAttribute> findAllByUserId(@Param("userId") String UserId);

}
