package com.admin.controller;

import com.admin.model.AdminService;
import com.admin.model.AdminVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> login(@RequestParam("account") String account, @RequestParam("password") String password, HttpSession session) {
        AdminVO admin = adminService.findByAccount(account);
        if (admin != null && admin.getAccount().equals(account) && admin.getPassword().equals(password)) {
            session.setAttribute("admin", admin);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "登入成功");
            return ResponseEntity.ok(response);
        } else {
            // 登入失敗，回傳 JSON 格式的錯誤資訊
            Map<String, Object> response = new HashMap<>();
            response.put("status", "failure");
            response.put("message", "帳號或密碼錯誤");

            return ResponseEntity.status(401).body(response);
        }
    }
}
