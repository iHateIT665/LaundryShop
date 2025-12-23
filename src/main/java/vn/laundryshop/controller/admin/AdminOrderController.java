package vn.laundryshop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.laundryshop.entity.*;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.service.IUserService;
import vn.laundryshop.service.impl.OrderService;
import vn.laundryshop.service.impl.PriceListService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final IOrderRepository orderRepo;
    private final IUserService userService;
    private final PriceListService priceService;

    // 1. DANH SÁCH & TÌM KIẾM
    @GetMapping
    public String listOrders(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String status,
                             Model model) {
        List<Order> orders;
        if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) {
            orders = orderRepo.searchOrders(keyword, status);
        } else {
            orders = orderService.getAllOrders();
        }
        model.addAttribute("orders", orders);
        return "admin/order/order-list"; // Đã sửa đường dẫn
    }

    // 2. CHI TIẾT ĐƠN HÀNG
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.findOrderById(id).orElseThrow();
        model.addAttribute("order", order);
        
        List<User> staffs = userService.getAllUsers().stream()
                                    .filter(u -> "STAFF".equals(u.getRole()))
                                    .collect(Collectors.toList());
        model.addAttribute("staffs", staffs);
        
        return "admin/order/order-detail"; // Đã sửa đường dẫn
    }

    // 3. CẬP NHẬT TRẠNG THÁI
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long orderId, 
                               @RequestParam String status,
                               @RequestParam(required = false) Long shipperId) {
        Order order = orderService.findOrderById(orderId).orElseThrow();
        order.setStatus(status);
        if (shipperId != null) {
            User shipper = userService.findById(shipperId).orElse(null);
            order.setDeliveryStaff(shipper);
        }
        orderService.save(order);
        return "redirect:/admin/orders/detail/" + orderId;
    }

    // 4. HIỂN THỊ FORM TẠO ĐƠN
    @GetMapping("/add")
    public String showCreateOrderForm(Model model) {
        List<User> customers = userService.getAllUsers().stream()
                .filter(u -> "CUSTOMER".equals(u.getRole()))
                .collect(Collectors.toList());
        model.addAttribute("customers", customers);
        model.addAttribute("prices", priceService.getAllPrices());
        
        return "admin/order/order-create"; // Đã sửa đường dẫn
    }

    // 5. XỬ LÝ TẠO ĐƠN (PHẦN QUAN TRỌNG ĐÃ ĐƯỢC KHÔI PHỤC)
    @PostMapping("/create")
    public String adminCreateOrder(@RequestParam Long customerId,
                                   @RequestParam String deliveryAddress,
                                   @RequestParam(name = "priceIds") List<Long> priceIds,
                                   @RequestParam(name = "quantities") List<Float> quantities) {
        
        // Tìm khách hàng
        User customer = userService.findById(customerId).orElseThrow();
        
        // Khởi tạo đơn hàng
        Order order = new Order();
        order.setCustomer(customer);
        order.setDeliveryAddress(deliveryAddress);
        order.setPickupAddress(deliveryAddress); // Mặc định lấy tại địa chỉ giao
        order.setStatus("CONFIRMED"); // Admin tạo thì xác nhận luôn
        order.setCreatedAt(LocalDateTime.now());

        // Xử lý danh sách chi tiết
        List<OrderDetail> details = new ArrayList<>();
        double totalAmount = 0;

        if (priceIds != null && !priceIds.isEmpty()) {
            for (int i = 0; i < priceIds.size(); i++) {
                Long pId = priceIds.get(i);
                Float qty = quantities.get(i);

                // Tìm thông tin giá
                PriceList pl = priceService.findById(pId).orElse(null);
                if (pl != null) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(order); // Liên kết ngược (QUAN TRỌNG)
                    detail.setPriceList(pl);
                    detail.setQuantity(qty);
                    detail.setUnitPrice(pl.getPrice());
                    detail.setSubtotal(pl.getPrice() * qty);
                    
                    totalAmount += detail.getSubtotal();
                    details.add(detail);
                }
            }
        }

        order.setOrderDetails(details);
        order.setTotalAmount(totalAmount);
        
        // Lưu xuống DB
        orderService.save(order);

        return "redirect:/admin/orders";
    }
}