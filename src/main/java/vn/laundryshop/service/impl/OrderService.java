package vn.laundryshop.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.Order;
import vn.laundryshop.repository.IOrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IOrderRepository orderRepo;

    public List<Order> getAllOrders() {
        return orderRepo.findAllByOrderByCreatedAtDesc();
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
    
    // Hàm lưu đơn hàng (dùng cho cập nhật shipper,...)
    public void save(Order order) {
        orderRepo.save(order);
    }
}