package com.admin.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository  extends JpaRepository<AdminVO, Integer> {
    AdminVO findByAccount(String account);
}
