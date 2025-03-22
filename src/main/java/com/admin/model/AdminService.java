package com.admin.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("adminService")
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;



    public AdminVO findByAccount(String account) {
        return adminRepository.findByAccount(account);
    }
}
