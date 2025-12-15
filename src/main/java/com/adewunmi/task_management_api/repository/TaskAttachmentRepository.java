package com.adewunmi.task_management_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adewunmi.task_management_api.entity.TaskAttachment;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTaskId(Long taskId);

    @Query("SELECT a FROM TaskAttachment a WHERE a.task.id = :taskId AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    Page<TaskAttachment> findByTaskIdAndDeletedAtIsNull(@Param("taskId") Long taskId, Pageable pageable);

    @Query("SELECT a FROM TaskAttachment a WHERE a.task.id = :taskId AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<TaskAttachment> findByTaskIdAndDeletedAtIsNullOrderByCreatedAtDesc(@Param("taskId") Long taskId);

    @Query("SELECT a FROM TaskAttachment a WHERE a.id = :id AND a.task.tenant.id = :tenantId AND a.deletedAt IS NULL")
    Optional<TaskAttachment> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(a) FROM TaskAttachment a WHERE a.task.id = :taskId AND a.deletedAt IS NULL")
    Long countByTaskIdAndDeletedAtIsNull(@Param("taskId") Long taskId);
}
