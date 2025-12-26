package vn.laundryshop.repository;

import vn.laundryshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// IMPORT THÊM ĐỂ DÙNG PAGE
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
	
	
    Optional<User> findByPhone(String phone);
 // Tìm kiếm theo tên, số điện thoại hoặc địa chỉ (chỉ lấy user đang hoạt động)
    Page<User> findByIsActiveTrueAndFullNameContainingOrPhoneContainingOrAddressContaining(
        String name, String phone, String address, Pageable pageable);

    // --- BỔ SUNG HÀM NÀY ĐỂ SỬA LỖI ---
    List<User> findByRole(String role);
    // ----------------------------------

    long countByRole(String role);
    
    // Hàm tìm kiếm nâng cao (nếu bạn dùng chức năng tìm kiếm)
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(:keyword IS NULL OR :keyword = '' OR u.fullName LIKE CONCAT('%', :keyword, '%') OR u.phone LIKE CONCAT('%', :keyword, '%') OR u.address LIKE CONCAT('%', :keyword, '%')) " +
           "AND (:role IS NULL OR :role = '' OR u.role = :role)")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") String role);

    // Hàm lấy danh sách User đang hoạt động (Có phân trang)
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    // Hàm lấy danh sách User đang hoạt động (Không phân trang - dùng cho dropdown list nếu cần)
    List<User> findByIsActiveTrue();
    User findByEmail(String email); // Thêm mới
    User findByResetPasswordToken(String token);
}