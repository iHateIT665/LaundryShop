package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
  
    public Page<User> searchUsers(String keyword, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        return userRepository.findByIsActiveTrueAndFullNameContainingOrPhoneContainingOrAddressContaining(
            keyword, keyword, keyword, pageable);
    }

    @Override
    public User save(User user) {
        // 1. Xử lý khi tạo mới (userId == null)
        if (user.getUserId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setIsActive(true); // <--- Mặc định kích hoạt khi tạo mới
        } else {
            // 2. Xử lý khi cập nhật (userId != null)
            User existingUser = userRepository.findById(user.getUserId()).get();
            
            // Xử lý mật khẩu
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // Lưu ý: Khi update, ta tôn trọng giá trị isActive mà user gửi lên
            // (không cần code set true/false mặc định ở đây nữa)
        }

        // Mặc định vai trò nếu thiếu
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CLIENT");
        }
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }


    @Override
    public Page<User> getAllUsers(int pageNo) {
        // Tạo PageRequest: trang số pageNo, mỗi trang 5 phần tử
        Pageable pageable = PageRequest.of(pageNo, 5);
        return userRepository.findByIsActiveTrue(pageable);
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
 // 1. Hàm cập nhật token
    public void updateResetPasswordToken(String token, String email) throws Exception {
        // Sửa userRepo -> userRepository
        User user = userRepository.findByEmail(email); 
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user); 
        } else {
            throw new Exception("Không tìm thấy khách hàng với email: " + email);
        }
    }

    // 2. Hàm lấy user từ token
    public User getByResetPasswordToken(String token) {
        // Sửa userRepo -> userRepository
        return userRepository.findByResetPasswordToken(token);
    }

    // 3. Hàm cập nhật mật khẩu mới (QUAN TRỌNG NHẤT)
 // Trong UserServiceImpl.java
    public void updatePassword(User user, String newPassword) {
        // Dùng biến đã inject, KHÔNG new BCryptPasswordEncoder()
        String encodedPassword = passwordEncoder.encode(newPassword); 
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }


}