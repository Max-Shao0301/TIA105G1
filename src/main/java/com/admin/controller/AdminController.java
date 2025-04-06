package com.admin.controller;

import com.admin.model.AdminService;
import com.admin.model.AdminVO;
import com.apply.model.ApplyService;
import com.apply.model.ApplyVO;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.orders.model.OrdersService;
import com.orders.model.OrdersVO;
import com.staff.model.StaffService;
import com.staff.model.StaffVO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ApplyService applyService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/admin/home/page")
    public String homePage(Model model) {
        /*顯示履歷列表*/
        List<ApplyVO> applyList = applyService.getAll();
        model.addAttribute("applyList", applyList);

        /*顯示服務人員列表*/
        List<StaffVO> staffList = staffService.getAll();
        model.addAttribute("staffList", staffList);

        /*顯示會員列表*/
        List<MemberVO> memberList = memberService.getAll();
        model.addAttribute("memberList", memberList);

        /*顯示訂單列表*/
        List<OrdersVO> ordersList = ordersService.getAll();
        model.addAttribute("ordersList", ordersList);

        return "/back-end/platform/adminHomepage";
    }

    @GetMapping("/admin/login/page")
    public String loginPage() {
        return "/back-end/platform/adminLogin";
    }



    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam("account") String account, @RequestParam("password") String password, HttpSession session) {
        AdminVO admin = adminService.findByAccount(account);
        if (admin != null && admin.getAccount().equals(account) && passwordEncoder.matches(password, admin.getPassword())) {
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

    @GetMapping("/BlobReader")
    public void dbGifReader(@RequestParam("applyId") Integer applyId, HttpServletResponse res) {
        res.setContentType("image/gif");
        ServletOutputStream out = null;
        try{
            out = res.getOutputStream();
            //透過Service查找單筆資料取得id在查詢該id的駕照圖片
            byte[] image = applyService.getOne(applyId).getLicense();
            if (image != null && image.length > 0) {
                out.write(image);//將圖片的二進位資料輸出到 HttpServletResponse
            } else {
                //如果沒讀取到圖片則放上一張預設圖片
                InputStream noImage = getClass().getResourceAsStream("/static/images/noImage.jpeg");
                if (noImage != null) {
                    out.write(noImage.readAllBytes());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally { //關閉流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }


}
