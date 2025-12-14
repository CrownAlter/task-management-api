package com.adewunmi.task_management_api.repository;

import com.adewunmi.task_management_api.entity.Task;
import com.adewunmi.task_management_api.enums.TaskStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.tenant.id = :tenantId AND t.deletedAt IS NULL")
    Page<Task> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.tenant.id = :tenantId AND t.deletedAt IS NULL")
    Optional<Task> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.tenant.id = :tenantId AND t.deletedAt IS NULL")
    Page<Task> findByAssignedToIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId,
            Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.tenant.id = :tenantId AND t.deletedAt IS NULL")
    Page<Task> findByStatusAndTenantId(@Param("status") TaskStatus status, @Param("tenantId") Long tenantId,
            Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end AND t.tenant.id = :tenantId AND t.deletedAt IS NULL")
    List<Task> findTasksDueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
            @Param("tenantId") Long tenantId);

}
