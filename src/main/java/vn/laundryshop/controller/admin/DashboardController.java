package vn.laundryshop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.laundryshop.dto.StatsDTO;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final IOrderRepository orderRepo;
    private final IUserRepository userRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // --- 1. SỐ LIỆU TỔNG QUAN ---
        long totalOrders = orderRepo.countTotalOrders();
        long totalUsers = userRepo.count();
        long pendingOrders = orderRepo.countPendingOrders();
        Double totalRevenueDb = orderRepo.sumTotalRevenue();
        
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenueDb != null ? totalRevenueDb : 0.0);

        // --- 2. BIỂU ĐỒ DOANH THU 7 NGÀY ---
        // Tính ngày bắt đầu tại Java (tránh lỗi JPQL không hiểu phép trừ ngày)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<StatsDTO> revenueData = orderRepo.getRevenueLast7Days(sevenDaysAgo);
        
        model.addAttribute("chartDateLabels", revenueData.stream().map(StatsDTO::getLabel).collect(Collectors.toList()));
        model.addAttribute("chartDateValues", revenueData.stream().map(StatsDTO::getValue).collect(Collectors.toList()));

        // --- 3. BIỂU ĐỒ DOANH THU THÁNG (Dùng Stored Procedure) ---
        // Gọi Native Query trả về List<Object[]>
        List<Object[]> rawMonthData = orderRepo.getRevenueByMonthNative();
        
        // Convert thủ công từ Object[] sang StatsDTO
        List<StatsDTO> monthData = new ArrayList<>();
        if (rawMonthData != null) {
            for (Object[] row : rawMonthData) {
                // row[0] là tháng (số), row[1] là tổng tiền
                monthData.add(new StatsDTO("Tháng " + row[0], (Number) row[1]));
            }
        }

        model.addAttribute("chartMonthLabels", monthData.stream().map(StatsDTO::getLabel).collect(Collectors.toList()));
        model.addAttribute("chartMonthValues", monthData.stream().map(StatsDTO::getValue).collect(Collectors.toList()));

        // --- 4. TOP DỊCH VỤ ---
        // Lấy top 5 ngay từ Database
        List<StatsDTO> serviceData = orderRepo.getTopServices(PageRequest.of(0, 5));
        
        model.addAttribute("chartServiceLabels", serviceData.stream().map(StatsDTO::getLabel).collect(Collectors.toList()));
        model.addAttribute("chartServiceValues", serviceData.stream().map(StatsDTO::getValue).collect(Collectors.toList()));

        return "admin/dashboard";
    }
}