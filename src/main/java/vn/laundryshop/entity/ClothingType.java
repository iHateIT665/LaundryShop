package vn.laundryshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "clothing_types")
public class ClothingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long typeId;

    @Column(nullable = false)
    private String typeName; 

    private String category; 
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- MỚI ---
    @Column(nullable = true, length = 64)
    private String image;

    @Transient
    public String getImagePath() {
        if (image == null || typeId == null) return "/images/default-clothing.png";
        return "/uploads/types/" + typeId + "/" + image;
    }
}