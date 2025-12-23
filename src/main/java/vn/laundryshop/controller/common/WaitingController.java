package vn.laundryshop.controller.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WaitingController {

    @GetMapping("/waiting")
    public String navigateAfterLogin(Authentication authentication) {
        // Lấy quyền (Role) của user vừa đăng nhập
        String role = "";
        
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            role = authority.getAuthority(); 
            // Lưu ý: Spring Security thường lưu role dạng "ROLE_ADMIN", "ROLE_STAFF" 
            // hoặc "ADMIN", "STAFF" tùy cách bạn cấu hình trong CustomUserDetails.
        }

        // 1. Nếu là ADMIN -> Vào Dashboard
        if (role.contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        
        // 2. Nếu là STAFF -> Vào Trang chủ Staff (ĐÂY LÀ PHẦN BẠN CẦN)
        if (role.contains("STAFF")) {
            return "redirect:/staff/home";
        }

        // 3. Mặc định (Khách hàng) -> Về trang chủ
        return "redirect:/home";
    }
}