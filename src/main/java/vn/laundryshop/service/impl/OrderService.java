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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IOrderRepository orderRepo;
    private final IUserRepository userRepo;



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
    public Page<Order> getAllOrders(int pageNo, String keyword) {
        // Sắp xếp giảm dần theo ngày tạo
        Pageable pageable = PageRequest.of(pageNo, 5, Sort.by("createdAt").descending());
        
        if (keyword != null && !keyword.isEmpty()) {
            // Nếu có từ khóa -> Gọi hàm search đã sửa ở Repo
            return orderRepo.searchOrders(keyword, pageable);
        }
        
        // Nếu không có từ khóa -> Lấy tất cả
        return orderRepo.findAll(pageable);
    }
   
   
}