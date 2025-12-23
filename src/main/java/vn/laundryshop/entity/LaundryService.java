package vn.laundryshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "services")
public class LaundryService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false)
    private String serviceName; 

    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- MỚI: Cột lưu tên ảnh ---
    @Column(nullable = true, length = 64)
    private String image;

    // --- MỚI: Hàm lấy đường dẫn ảnh để hiển thị ---
    @Transient
    public String getImagePath() {
        if (image == null || serviceId == null) return "/images/default-service.png"; // Ảnh mặc định
        return "/uploads/services/" + serviceId + "/" + image;
    }
}