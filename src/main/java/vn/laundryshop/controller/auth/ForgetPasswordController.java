package vn.laundryshop.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.laundryshop.entity.User;
import vn.laundryshop.service.impl.UserServiceImpl;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgetPasswordController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserServiceImpl userService;

    // 1. Hiển thị form nhập email
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "auth/forgot_password_form";
    }

    // 2. Xử lý khi nhấn nút "Gửi mã"
    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = UUID.randomUUID().toString();

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            
            // Gửi email
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "Chúng tôi đã gửi link reset mật khẩu vào email của bạn.");

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }

        return "auth/forgot_password_form";
    }

    // 3. Hiển thị form đổi mật khẩu (từ link email)
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("title", "Token không hợp lệ");
            model.addAttribute("message", "Link reset mật khẩu không đúng hoặc đã hết hạn.");
            return "auth/message"; // Cần tạo trang thông báo lỗi đơn giản
        }
        model.addAttribute("token", token);
        return "auth/reset_password_form";
    }

    // 4. Xử lý đổi mật khẩu mới
    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("title", "Lỗi");
            model.addAttribute("message", "Token không hợp lệ.");
            return "auth/message";
        } else {
            userService.updatePassword(user, password);
            model.addAttribute("message", "Bạn đã đổi mật khẩu thành công. Hãy đăng nhập lại.");
        }

        return "auth/login";
    }

    // Hàm tiện ích gửi mail
    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("contact@laundryshop.vn", "Laundry Shop Support");
        helper.setTo(recipientEmail);

        String subject = "Đây là link reset mật khẩu của bạn";

        String content = "<p>Xin chào,</p>"
                + "<p>Bạn đã yêu cầu reset mật khẩu.</p>"
                + "<p>Nhấn vào link dưới đây để đổi mật khẩu:</p>"
                + "<p><a href=\"" + link + "\">Đổi mật khẩu</a></p>"
                + "<br>"
                + "<p>Bỏ qua email này nếu bạn không yêu cầu, "
                + "hoặc nếu bạn nhớ ra mật khẩu cũ.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}