package vn.laundryshop.controller.client;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.laundryshop.dto.CartItem;
import vn.laundryshop.entity.*;
import vn.laundryshop.service.*;
import vn.laundryshop.service.impl.OrderService;
import vn.laundryshop.service.impl.PriceListService;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.repository.IUserRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientOrderController {

    private final PriceListService priceService;
    private final OrderService orderService;
    private final IUserRepository userRepo; // Để tìm thông tin người đang đăng nhập
    private final IOrderRepository orderRepo;

    // 1. Hiển thị trang đặt hàng (Menu)
    @GetMapping("/booking")
    public String showBookingPage(Model model) {
        model.addAttribute("prices", priceService.getAllPrices()); // Lấy bảng giá làm Menu
        return "client/booking";
    }

    // 2. Thêm vào giỏ hàng
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long priceId, 
                            @RequestParam Integer quantity, 
                            HttpSession session) {
        
        // Lấy giỏ hàng từ Session (nếu chưa có thì tạo mới)
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // Tìm món hàng trong DB để lấy thông tin chi tiết
        PriceList priceItem = priceService.findById(priceId).orElse(null);
        if (priceItem != null) {
            // Kiểm tra xem món này đã có trong giỏ chưa?
            boolean exists = false;
            for (CartItem item : cart) {
                if (item.getPriceId().equals(priceId)) {
                    item.setQuantity(item.getQuantity() + quantity); // Cộng dồn số lượng
                    exists = true;
                    break;
                }
            }

            // Nếu chưa có thì thêm mới
            if (!exists) {
                cart.add(new CartItem(
                    priceItem.getPriceId(),
                    priceItem.getService().getServiceName(),
                    priceItem.getClothingType().getTypeName(),
                    priceItem.getPrice(),
                    priceItem.getUnit(),
                    quantity
                ));
            }
        }
        
        // Lưu ngược lại vào Session
        session.setAttribute("cart", cart);
        
        return "redirect:/client/booking?added=true";
    }

    // 3. Xem Giỏ Hàng
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        // Tính tổng tiền
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "client/cart";
    }

    // 4. Xóa món khỏi giỏ
    @GetMapping("/cart/remove/{index}")
    public String removeFromCart(@PathVariable int index, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            cart.remove(index);
        }
        return "redirect:/client/cart";
    }

    // 5. CHECKOUT (Lưu đơn hàng)
    @PostMapping("/checkout")
    public String checkout(@RequestParam String address, 
                           @RequestParam String note,
                           HttpSession session, Principal principal) {
        
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/client/booking";
        }

        // Tìm khách hàng đang đăng nhập
        String phone = principal.getName();
        User customer = userRepo.findByPhone(phone).orElseThrow();

        // A. Tạo Đơn Hàng (Order)
        Order order = new Order();
        order.setCustomer(customer);
        order.setDeliveryAddress(address); // Địa chỉ khách nhập lúc checkout
        order.setPickupAddress(address);   // Tạm coi địa chỉ lấy = địa chỉ giao
        order.setStatus("PENDING");
        
        // Tính tổng tiền
        double totalAmount = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        order.setTotalAmount(totalAmount);
        
        // B. Tạo Chi Tiết Đơn Hàng (OrderDetails)
        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cart) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order); // Liên kết ngược
            detail.setPriceList(priceService.findById(item.getPriceId()).orElseThrow());
            detail.setQuantity(item.getQuantity().floatValue());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setSubtotal(item.getSubtotal());
            
            details.add(detail);
        }
        
        order.setOrderDetails(details); // Gán list chi tiết vào đơn hàng

        // C. Lưu xuống DB
        orderService.save(order);

        // D. Xóa giỏ hàng sau khi đặt xong
        session.removeAttribute("cart");

        return "redirect:/client/success"; // Trang thông báo thành công
    }

    @GetMapping("/success")
    public String success() {
        return "client/success";
    }
    @GetMapping("/history")
    public String viewHistory(Model model, Principal principal) {
        // Tìm khách hàng đang đăng nhập
        String phone = principal.getName();
        User customer = userRepo.findByPhone(phone).orElseThrow();

        // Lấy danh sách đơn của họ
        List<Order> myOrders = orderRepo.findByCustomerOrderByCreatedAtDesc(customer);
        
        model.addAttribute("orders", myOrders);
        return "client/history";
    }

    // 👇 2. XEM CHI TIẾT 1 ĐƠN HÀNG
    @GetMapping("/history/{id}")
    public String viewOrderHistoryDetail(@PathVariable Long id, Model model, Principal principal) {
        String phone = principal.getName();
        User customer = userRepo.findByPhone(phone).orElseThrow();

        // Tìm đơn hàng và bảo mật (chỉ xem được đơn của chính mình)
        Order order = orderRepo.findById(id).orElseThrow();
        
        if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
            return "redirect:/client/history?error=unauthorized";
        }

        model.addAttribute("order", order);
        return "client/order-detail";
    }
 // Thêm vào ClientOrderController
    @GetMapping("/history/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Principal principal) {
        String phone = principal.getName();
        User customer = userRepo.findByPhone(phone).orElseThrow();
        Order order = orderRepo.findById(id).orElseThrow();

        // 1. Kiểm tra chính chủ
        if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
            return "redirect:/client/history?error=unauthorized";
        }

        // 2. Chỉ cho hủy nếu đang PENDING
        if ("PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            orderRepo.save(order);
            return "redirect:/client/history?message=cancelled";
        } else {
            return "redirect:/client/history?error=cannot_cancel";
        }
    }
}