package vn.laundryshop.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.laundryshop.entity.User;
import vn.laundryshop.service.IUserService;
import vn.laundryshop.util.FileUploadUtil;

import java.io.IOException;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model, 
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "keyword", required = false) String keyword) {
        Page<User> pageUser;
        if (keyword != null && !keyword.isEmpty()) {
            pageUser = userService.searchUsers(keyword, page);
            model.addAttribute("keyword", keyword);
        } else {
            pageUser = userService.getAllUsers(page);
        }
        model.addAttribute("users", pageUser.getContent());
        model.addAttribute("pageData", pageUser);
        return "admin/user/user-list";
    }

    // 2. Hiển thị Form Thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user/user-form";
    }

    // 3. Xử lý Lưu User (Có Validation & Upload ảnh)
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           @RequestParam("imageFile") MultipartFile multipartFile,
                           Model model) throws IOException {

        // Kiểm tra lỗi Validation (ví dụ: SĐT không đúng 10 số, tên trống...)
        if (bindingResult.hasErrors()) {
            // Logic xử lý riêng cho mật khẩu khi EDIT (đã hướng dẫn ở bước trước)
            boolean isEdit = user.getUserId() != null;
            boolean onlyPassError = bindingResult.getErrorCount() == 1 && bindingResult.hasFieldErrors("password");
            
            if (!(isEdit && onlyPassError && (user.getPassword() == null || user.getPassword().isEmpty()))) {
                // TRƯỜNG HỢP CÓ LỖI THẬT: Trả về form để người dùng thấy lỗi
                model.addAttribute("user", user); // Đảm bảo dữ liệu cũ vẫn nằm trong các ô input
                return "admin/user/user-form"; 
            }
        }

        // Nếu không lỗi thì mới thực hiện lưu
        User savedUser = userService.save(user);

        // 5. Xử lý Upload ảnh (nếu có chọn file)
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            
            // Cập nhật tên ảnh vào User và lưu lại lần nữa
            savedUser.setAvatar(fileName);
            userService.save(savedUser); 

            // Lưu file ảnh vật lý vào thư mục
            String uploadDir = "uploads/user-photos/" + savedUser.getUserId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        return "redirect:/admin/users";
    }

    // 6. Hiển thị Form Chỉnh sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Id:" + id));
        model.addAttribute("user", user);
        return "admin/user/user-form";
    }

    // 7. Xóa User (Soft delete)
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}