package vn.laundryshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Entity
@Table(name = "services")
public class LaundryService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String serviceName;

    private String description;
    
    private String image;
    
  

    // --- BỔ SUNG ACTIVE ĐỂ SOFT DELETE ---
    @Column(name = "is_active")
    private Boolean isActive = true; 
    // -------------------------------------

    @Transient
    public String getImagePath() {
        if (image == null || serviceId == null) return "/images/default-service.png";
        return "/uploads/services/" + serviceId + "/" + image;
    }
}