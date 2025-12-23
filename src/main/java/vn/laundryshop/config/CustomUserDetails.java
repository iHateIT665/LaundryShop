package vn.laundryshop.config;

import vn.laundryshop.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }
    public User getUser() {
        return this.user;
    }

    // 1. Lấy quyền (Role). Spring Security yêu cầu role phải có tiền tố "ROLE_"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // 2. Lấy mật khẩu để Spring tự so sánh
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 3. Lấy tên đăng nhập (ở đây là Số điện thoại)
    @Override
    public String getUsername() {
        return user.getPhone();
    }

    // Các cài đặt khác cứ để mặc định là true (Tài khoản không bị khóa, không hết hạn...)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
   
 

    @Override
    public boolean isEnabled() {
        return user.getIsActive(); // Nếu isActive = false -> Chặn đăng nhập luôn!
    }
}