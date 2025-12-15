package com.adewunmi.task_management_api.repository;

import com.adewunmi.task_management_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndTenantId(String email, Long tenantId);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.tenant.id = :tenantId AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmailAndTenantId(@Param("email") String email, @Param("tenantId") Long tenantId);

    boolean existsByEmailAndTenantId(String email, Long tenantId);

    List<User> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.tenant.id = :tenantId AND u.deletedAt IS NULL")
    long countActiveUsersByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.tenant.id = :tenantId AND u.deletedAt IS NULL")
    Optional<User> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);

    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.deletedAt IS NULL")
    Page<User> findByTenantIdAndDeletedAtIsNull(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "u.deletedAt IS NULL")
    Page<User> searchByNameOrEmail(@Param("query") String query, @Param("tenantId") Long tenantId, Pageable pageable);

}
