package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IOrderRepository;
import vn.laundryshop.repository.IUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IOrderRepository orderRepo;
    private final IUserRepository userRepo;

    // --- SỬA HÀM NÀY ĐỂ PHÂN TRANG ---
    public Page<Order> getAllOrders(int pageNo) {
        // Sắp xếp giảm dần theo ngày tạo (mới nhất lên đầu)
        // 5 phần tử / trang
        Pageable pageable = PageRequest.of(pageNo, 5, Sort.by("createdAt").descending());
        
        // Gọi hàm findAll có sẵn của JpaRepository
        return orderRepo.findAll(pageable);
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepo.findById(id);
    }

    public void updateStatus(Long orderId, String newStatus) {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(newStatus);
            orderRepo.save(order);
        }
    }
    
    // Hàm giao việc cho nhân viên
    public void assignStaff(Long orderId, Long staffId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        User staff = userRepo.findById(staffId).orElse(null);
        
        if (order != null && staff != null) {
            order.setStaff(staff);
            order.setStatus("CONFIRMED"); // Giao xong thì tự động chuyển trạng thái đã duyệt
            orderRepo.save(order);
        }
    }
    
    public void save(Order order) {
        orderRepo.save(order);
    }
}