package vn.laundryshop.controller.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IUserRepository;
import vn.laundryshop.util.FileUploadUtil; // Đừng quên tạo class này như hướng dẫn trước

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final IUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    // 1. Hiển thị trang hồ sơ
    @GetMapping
    public String showProfile(Model model, Principal principal) {
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone).orElseThrow();
        model.addAttribute("user", user);
        return "common/profile"; 
    }

    // 2. Cập nhật thông tin (Đã thêm xử lý ảnh)
    @PostMapping("/update")
    public String updateInfo(@RequestParam String fullName,
                             @RequestParam String address,
                             @RequestParam(value = "image", required = false) MultipartFile multipartFile, // Thêm nhận file ảnh
                             Principal principal,
                             RedirectAttributes redirectAttributes) throws IOException { // Thêm throws IOException
        
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone).orElseThrow();

        // Cập nhật thông tin text
        user.setFullName(fullName);
        user.setAddress(address);
        
        // --- XỬ LÝ UPLOAD ẢNH ---
        // Chỉ xử lý nếu người dùng có chọn file
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setAvatar(fileName); // Lưu tên file vào DB
            
            // Lưu file vật lý
            // Đường dẫn: uploads/user-photos/{userId}/
            String uploadDir = "uploads/user-photos/" + user.getUserId();
            
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        // -------------------------

        userRepo.save(user);
        
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        return "redirect:/profile";
    }

    // 3. Đổi mật khẩu (Giữ nguyên)
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "redirect:/profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}