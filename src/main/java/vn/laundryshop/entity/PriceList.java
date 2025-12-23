package vn.laundryshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "price_list", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"service_id", "type_id"}) // Chặn trùng lặp (1 dịch vụ + 1 loại đồ chỉ có 1 giá)
})
public class PriceList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    // Liên kết với Dịch Vụ
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private LaundryService service;

    // Liên kết với Loại Đồ
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private ClothingType clothingType;

    @Column(nullable = false)
    private Double price; // Giá tiền

    @Column(nullable = false)
    private String unit; // Đơn vị: Kg, Cái, Bộ...
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}