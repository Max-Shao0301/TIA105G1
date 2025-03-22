package com.admin.controller;

import com.admin.model.AdminService;
import com.admin.model.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/home/page")
    public String homePage() {
        return "/back-end/platform/adminHomepage";
    }

    @GetMapping("/admin/login/page")
    public String loginPage() {
        return "/back-end/platform/adminLogin";
    }


    @PostMapping("/admin/login")
    public ResponseEntity<String> login2(@RequestParam("account") String account, @RequestParam("password") String password) {
        // 假設這裡做的是帳號和密碼的比對
        AdminVO admin = adminService.findByAccount("admin");
        if (admin != null && admin.getAccount().equals(account) && admin.getPassword().equals(password)) {
            // 登入成功，回傳 "success"
            return ResponseEntity.ok("success");

        } else {
            // 登入失敗，回傳 "failure"
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("failure");
        }
    }
}
