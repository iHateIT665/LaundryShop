package vn.laundryshop.controller.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.repository.IUserRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final IOrderRepository orderRepo;
    private final IUserRepository userRepo;

    // 👇 1. TRANG CHỦ (DASHBOARD) CHO STAFF
    @GetMapping("/home")
    public String staffHome(Model model, Principal principal) {
        String phone = principal.getName();
        User staff = userRepo.findByPhone(phone).orElseThrow();

        // Lấy tất cả đơn hàng của staff này
        List<Order> allTasks = orderRepo.findByDeliveryStaffOrderByCreatedAtDesc(staff);

        // Tính toán thống kê nhanh (bằng Java Stream cho gọn)
        long pendingCount = allTasks.stream()
                .filter(o -> "SHIPPING".equals(o.getStatus()) || "PROCESSING".equals(o.getStatus()))
                .count();

        long completedCount = allTasks.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .count();

        model.addAttribute("staff", staff);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);
        
        return "staff/home";
    }

    // 2. XEM DANH SÁCH ĐƠN HÀNG
    @GetMapping("/orders")
    public String myTasks(Model model, Principal principal) {
        String phone = principal.getName();
        User staff = userRepo.findByPhone(phone).orElseThrow();
        List<Order> myOrders = orderRepo.findByDeliveryStaffOrderByCreatedAtDesc(staff);
        
        model.addAttribute("orders", myOrders);
        model.addAttribute("staffName", staff.getFullName());
        return "staff/order-list";
    }

    // 3. CẬP NHẬT TRẠNG THÁI
    @PostMapping("/orders/update")
    public String updateTaskStatus(@RequestParam Long orderId, @RequestParam String status) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        order.setStatus(status);
        orderRepo.save(order);
        return "redirect:/staff/orders";
    }
    @GetMapping("/help")
    public String helpPage(Model model, Principal principal) {
        // Lấy thông tin Staff để hiển thị tên trên Navbar (nếu cần)
        String phone = principal.getName();
        User staff = userRepo.findByPhone(phone).orElseThrow();
        model.addAttribute("staff", staff);
        
        return "staff/help";
    }
}