package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>
{
    Role findByRole(String role);
}
