package vn.laundryshop.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.laundryshop.entity.User;
import vn.laundryshop.service.IUserService;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final IUserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
                                  BindingResult bindingResult,
                                  Model model) {

        // 1. Kiểm tra lỗi validate form (Annotation @NotBlank, @Size...)
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // 2. Kiểm tra nghiệp vụ: SĐT đã tồn tại chưa
        if (userService.findByPhone(user.getPhone()).isPresent()) {
            bindingResult.rejectValue("phone", "error.user", "Số điện thoại này đã được đăng ký!");
            return "auth/register";
        }

        // 3. Nếu không có lỗi thì lưu và chuyển trang
        userService.save(user);
        return "redirect:/login?success";
    }
}