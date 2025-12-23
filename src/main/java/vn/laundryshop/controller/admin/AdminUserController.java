package vn.laundryshop.controller.admin;

import vn.laundryshop.entity.User;
import vn.laundryshop.service.IUserService;
import vn.laundryshop.util.FileUploadUtil; // Import class tiện ích lưu file
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder; // Nếu có đổi mật khẩu

import java.io.IOException;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Dùng để mã hóa pass nếu tạo mới

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/user-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user/user-form";
    }

    // --- SỬA LOGIC LƯU (Có xử lý ảnh) ---
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        // Lưu thông tin text (Logic mật khẩu đã được xử lý bên trong Service.save)
        User savedUser = userService.save(user);

        // Xử lý upload ảnh nếu có
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            savedUser.setAvatar(fileName);
            userService.save(savedUser); // Lưu lại để cập nhật tên file avatar

            String uploadDir = "uploads/user-photos/" + savedUser.getUserId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        model.addAttribute("user", user);
        return "admin/user/user-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}