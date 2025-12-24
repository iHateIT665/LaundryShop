package vn.laundryshop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IUserRepository;
import vn.laundryshop.service.impl.OrderService;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private IUserRepository userRepository; 

    // --- SỬA HÀM LIST ORDERS ---
    @GetMapping
    public String listOrders(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        // Gọi hàm phân trang từ Service
        Page<Order> orderPage = orderService.getAllOrders(page);
        
        // Gửi danh sách đơn hàng của trang hiện tại sang View
        model.addAttribute("orders", orderPage.getContent());
        
        // Gửi thông tin trang (để vẽ nút phân trang) sang View
        model.addAttribute("pageData", orderPage);
        
        // Lấy danh sách STAFF để dùng cho Popup giao việc (ở danh sách)
        List<User> staffList = userRepository.findByRole("STAFF");
        model.addAttribute("staffList", staffList);
        
        // Truyền biến 'module' để Sidebar biết đang active mục nào
        model.addAttribute("module", "orders");
        
        return "admin/order/order-list";
    }

    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        model.addAttribute("order", order);
        
        // Lấy danh sách nhân viên để hiện trong dropdown giao việc (ở chi tiết)
        List<User> staffList = userRepository.findByRole("STAFF");
        model.addAttribute("staffList", staffList);
        
        return "admin/order/order-detail";
    }

    @PostMapping("/assign")
    public String assignStaff(@RequestParam("orderId") Long orderId, 
                              @RequestParam("staffId") Long staffId,
                              RedirectAttributes ra) {
        try {
            orderService.assignStaff(orderId, staffId);
            ra.addFlashAttribute("message", "Đã giao đơn hàng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }
    
    @GetMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable("id") Long id) {
        orderService.updateStatus(id, "CONFIRMED");
        return "redirect:/admin/orders/detail/" + id;
    }

    @GetMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Long id) {
        orderService.updateStatus(id, "CANCELLED");
        return "redirect:/admin/orders/detail/" + id;
    }
}