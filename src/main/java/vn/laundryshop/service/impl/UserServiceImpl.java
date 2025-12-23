package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IUserRepository;
import vn.laundryshop.service.IUserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        // 1. Xử lý khi tạo mới (userId == null)
        if (user.getUserId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 2. Xử lý khi cập nhật (userId != null)
            User existingUser = userRepository.findById(user.getUserId()).get();
            
            // Nếu mật khẩu từ form gửi về bị trống hoặc null -> Giữ pass cũ
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // Có nhập pass mới -> Mã hóa và lưu
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        // Mặc định vai trò
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CLIENT");
        }
        
        // Mặc định active
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    // --- ĐÃ SỬA ĐOẠN NÀY ---
    @Override
    public List<User> getAllUsers() {
        // Chỉ lấy những user có isActive = true (ẩn những người đã bị xóa/block)
        return userRepository.findByIsActiveTrue(); 
    }
    // -----------------------

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false); // Chuyển trạng thái thành false (Soft delete)
            userRepository.save(user);
        }
    }
}