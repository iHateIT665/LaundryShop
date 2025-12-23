package vn.laundryshop.controller.auth;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForgetPasswordController {
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "auth/forgot-password";
    }
    // Sau này thêm PostMapping để xử lý gửi mail tại đây
}