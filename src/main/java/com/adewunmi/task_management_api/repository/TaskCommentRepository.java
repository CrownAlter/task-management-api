package com.adewunmi.task_management_api.repository;

import com.adewunmi.task_management_api.entity.TaskComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    @Query("SELECT c FROM TaskComment c WHERE c.task.id = :taskId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<TaskComment> findByTaskIdAndDeletedAtIsNull(@Param("taskId") Long taskId, Pageable pageable);

    @Query("SELECT c FROM TaskComment c WHERE c.id = :id AND c.task.tenant.id = :tenantId AND c.deletedAt IS NULL")
    Optional<TaskComment> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(c) FROM TaskComment c WHERE c.task.id = :taskId AND c.deletedAt IS NULL")
    Long countByTaskIdAndDeletedAtIsNull(@Param("taskId") Long taskId);

}
