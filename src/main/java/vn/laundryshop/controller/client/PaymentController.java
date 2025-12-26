package vn.laundryshop.controller.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.laundryshop.entity.Order;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.service.impl.VnPayService;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VnPayService vnPayService;
    private final IOrderRepository orderRepo;

    // 1. Tạo yêu cầu thanh toán
    @GetMapping("/create/{orderId}")
    public String createPayment(@PathVariable Long orderId, HttpServletRequest request) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        
        // Lấy tổng tiền (ép kiểu về long để xử lý số nguyên)
        long amount = order.getTotalAmount().longValue();
        String orderInfo = "Thanh toan don hang " + orderId;
        
        // Tạo URL VNPAY
        String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderInfo);
        
        return "redirect:" + paymentUrl;
    }

    // 2. Xử lý kết quả trả về từ VNPAY
    @GetMapping("/vnpay_return")
    public String paymentReturn(HttpServletRequest request, RedirectAttributes ra) {
        String status = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        
        // Lấy mã đơn hàng từ chuỗi orderInfo (Cắt chuỗi "Thanh toan don hang X")
        String orderIdStr = orderInfo.replace("Thanh toan don hang ", "");
        Long orderId = Long.parseLong(orderIdStr);

        if ("00".equals(status)) {
            // Thanh toán thành công -> Cập nhật DB
            Order order = orderRepo.findById(orderId).orElse(null);
            if (order != null) {
                // Bạn có thể thêm cột payment_status vào bảng Orders nếu muốn chuẩn hơn
                // Ở đây mình tạm dùng ghi chú vào Status hiện tại hoặc giữ nguyên
                // Ví dụ: Nếu đơn đang PENDING -> chuyển sang CONFIRMED (đã trả tiền)
                if("PENDING".equals(order.getStatus())) {
               
                    order.setPaymentStatus("PAID");
               
                }
                orderRepo.save(order);
            }
            ra.addFlashAttribute("message", "Thanh toán thành công qua VNPAY!");
        } else {
            ra.addFlashAttribute("error", "Thanh toán thất bại hoặc bị hủy!");
        }
        
     
      
        return "redirect:/client/history/" + orderId;
    }
}