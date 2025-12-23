package vn.laundryshop.controller.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String performLogout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Lấy thông tin người dùng hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // 2. Gọi hàm logout của Spring Security để xóa Session, xóa Cookies
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // 3. Chuyển hướng về trang đăng nhập kèm thông báo
        return "redirect:/login?logout=true";
    }
}