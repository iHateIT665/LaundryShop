package vn.laundryshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    private String address;
    private String role; // ADMIN, STAFF, CLIENT

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- MỚI: Cột lưu tên ảnh đại diện ---
    @Column(length = 64)
    private String avatar;

    // --- MỚI: Hàm lấy đường dẫn ảnh để hiển thị ---
    @Transient
    public String getAvatarPath() {
        // Nếu chưa có ảnh, trả về ảnh mặc định (bạn nên có file này trong static/images)
        if (avatar == null || userId == null) return "/images/default-user.png";
        
        // Trả về đường dẫn: /uploads/user-photos/{id}/{avatar}
        return "/uploads/user-photos/" + userId + "/" + avatar;
    }
}