package vn.laundryshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @Column(nullable = false, unique = true, length = 15)
    @NotBlank(message = "Số điện thoại là bắt buộc")
    private String phone;

    @Column(nullable = false)
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Column(length = 20)
    private String role; // "ADMIN", "STAFF", "CLIENT"

    @Column(nullable = true, length = 64)
    private String avatar;

    // --- SỬA LẠI ĐOẠN NÀY ---
    // Thay vì biến 'enabled', dùng 'isActive' để khớp với code Repository
    @Column(name = "is_active")
    private Boolean isActive = true; 
    // ------------------------

    @Transient
    public String getAvatarPath() {
        if (avatar == null || userId == null) return "/images/default-user.png";
        return "/uploads/user-photos/" + userId + "/" + avatar;
    }


    @Column(unique = true, length = 150)
    private String email;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;
}