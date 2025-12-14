package com.adewunmi.task_management_api.repository;

import com.adewunmi.task_management_api.entity.Role;
import com.adewunmi.task_management_api.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}
