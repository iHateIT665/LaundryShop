package vn.laundryshop.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.laundryshop.config.CustomUserDetails;
import vn.laundryshop.entity.Complaint;
import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.service.IUserService;
import vn.laundryshop.service.impl.ComplaintService;
import vn.laundryshop.util.FileUploadUtil;

import java.io.IOException;

@Controller
@RequestMapping("/complaint")
@RequiredArgsConstructor
public class ClientComplaintController {

    private final ComplaintService complaintService;
    private final IUserService userService;
    private final IOrderRepository orderRepo; // Thêm Repository để lấy thông tin đơn hàng

    // 1. Xem lịch sử khiếu nại
    @GetMapping("/history")
    public String history(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login"; // Nếu không tìm thấy user -> về trang login

        model.addAttribute("complaints", complaintService.getComplaintsByUser(user));
        return "client/complaint-history";
    }

    // 2. Trang tạo khiếu nại (Nhận orderId từ URL nếu có)
    @GetMapping("/create")
    public String createForm(Model model, @RequestParam(value = "orderId", required = false) Long orderId) {
        Complaint complaint = new Complaint();
        
        // Nếu khách bấm nút "Khiếu nại" từ đơn hàng cụ thể
        if (orderId != null) {
            Order order = orderRepo.findById(orderId).orElse(null);
            if (order != null) {
                complaint.setOrder(order);
                complaint.setTitle("Khiếu nại về đơn hàng #" + orderId); // Gợi ý tiêu đề
            }
        }
        
        model.addAttribute("complaint", complaint);
        return "client/complaint-form";
    }

    // 3. Xử lý gửi khiếu nại
    @PostMapping("/save")
    public String saveComplaint(@ModelAttribute("complaint") Complaint complaint,
                                @RequestParam("imageFile") MultipartFile multipartFile,
                                @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            System.out.println("Lỗi: Không xác định được người dùng hiện tại.");
            return "redirect:/login";
        }
        
        complaint.setUser(user);
        
        // Xử lý upload ảnh (nếu khách có chọn ảnh)
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            complaint.setImage(fileName);
            
            // Lưu trước để tạo ID cho Complaint
            complaintService.saveClientComplaint(complaint);
            
            // Tạo thư mục lưu ảnh theo ID khiếu nại
            String uploadDir = "uploads/complaints/" + complaint.getComplaintId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            // Lưu bình thường nếu không có ảnh
            complaintService.saveClientComplaint(complaint);
        }

        return "redirect:/complaint/history?success";
    }

    /**
     * Hàm phụ: Lấy User từ Session (Phiên bản dùng Optional)
     */
    private User getAuthenticatedUser(CustomUserDetails userDetails) {
        if (userDetails == null) return null;
        
        String username = userDetails.getUsername();
        
        // 1. Tìm bằng SĐT (Trả về Optional)
        // Nếu có user (isPresent) thì lấy ra (.get()), nếu không thì tìm tiếp bằng Email
        return userService.findByPhone(username)
                .orElseGet(() -> userService.findByEmail(username));
    }
}