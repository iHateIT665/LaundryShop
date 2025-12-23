package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor; // Nhớ import dòng này
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.laundryshop.config.CustomUserDetails;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IUserRepository;

@Service
@RequiredArgsConstructor // <--- QUAN TRỌNG: Lombok sẽ tự tạo Constructor ở đây
public class CustomUserDetailsService implements UserDetailsService {

    // <--- QUAN TRỌNG: Phải có chữ 'final' thì @RequiredArgsConstructor mới hoạt động
    private final IUserRepository userRepository; 

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        System.out.println("--- [DEBUG] Security đang tìm user với SĐT: " + phone);
        
        // Tìm user trong DB
        User user = userRepository.findByPhone(phone).orElse(null);

        if (user == null) {
            System.out.println("--- [DEBUG] LỖI: Không tìm thấy User trong DB!");
            throw new UsernameNotFoundException("Không tìm thấy user: " + phone);
        }

        System.out.println("--- [DEBUG] OK: Đã tìm thấy User ID: " + user.getUserId());
        return new CustomUserDetails(user);
    }
}