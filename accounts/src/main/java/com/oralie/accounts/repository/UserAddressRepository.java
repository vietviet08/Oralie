package com.oralie.accounts.repository;

import com.oralie.accounts.model.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    void deleteByUserId(String userId);
    void deleteByUsername(String username);

    @Query("SELECT u FROM UserAddress u WHERE u.userId = :userId")
    Page<UserAddress> findAllByUserId(@Param("userId") String UserId, Pageable pageable);

}
