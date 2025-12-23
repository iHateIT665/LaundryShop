package vn.laundryshop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.repository.IUserRepository;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IOrderRepository orderRepo;
    private final IUserRepository userRepo;

    @GetMapping
    public String dashboard(Model model) {
        // 1. Tổng đơn hàng
        long totalOrders = orderRepo.countTotalOrders();
        
        // 2. Doanh thu (Nếu chưa có đơn nào thì trả về 0)
        Double revenue = orderRepo.sumTotalRevenue();
        if (revenue == null) revenue = 0.0;
        
        // 3. Số đơn đang chờ xử lý (Để Admin biết cần làm việc ngay)
        long pendingOrders = orderRepo.countPendingOrders();
        
        // 4. Tổng số khách hàng
        long totalCustomers = userRepo.countByRole("CUSTOMER");

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("revenue", revenue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalCustomers", totalCustomers);

        return "admin/dashboard";
    }
}