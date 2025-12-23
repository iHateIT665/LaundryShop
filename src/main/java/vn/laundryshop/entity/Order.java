package vn.laundryshop.entity;
import java.util.List;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    // Ai đặt đơn này?
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Ai đi lấy đồ? (Có thể null nếu chưa ai nhận)
    @ManyToOne
    @JoinColumn(name = "pickup_staff_id")
    private User pickupStaff;

    // Ai đi giao đồ?
    @ManyToOne
    @JoinColumn(name = "delivery_staff_id")
    private User deliveryStaff;

    private String pickupAddress;
    private String deliveryAddress;
    
    private Double totalAmount; // Tổng tiền
    
    private String status; // PENDING, CONFIRMED, PROCESSING...
    
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo đơn
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Quan trọng: Ngắt vòng lặp log
    private List<OrderDetail> orderDetails;
}