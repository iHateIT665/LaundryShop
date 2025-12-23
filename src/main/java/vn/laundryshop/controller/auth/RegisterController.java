package vn.laundryshop.controller.auth;

import vn.laundryshop.entity.User;
import vn.laundryshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {
    
    @Autowired
    private IUserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user) {
        userService.save(user); // Giả sử service đã xử lý mã hóa pass
        return "redirect:/login?success";
    }
}