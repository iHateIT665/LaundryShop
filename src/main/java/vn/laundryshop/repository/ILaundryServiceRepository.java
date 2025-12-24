package vn.laundryshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.laundryshop.entity.LaundryService;
import java.util.List;

@Repository
public interface ILaundryServiceRepository extends JpaRepository<LaundryService, Long> {
    
    // Tìm dịch vụ active (có phân trang)
    Page<LaundryService> findByIsActiveTrue(Pageable pageable);
    
    // Tìm dịch vụ active (list thường)
    List<LaundryService> findByIsActiveTrue();

    // --- HÀM TÌM KIẾM ---
    // (Spring Data JPA sẽ tự sinh câu lệnh SQL dựa trên tên hàm này)
    Page<LaundryService> findByIsActiveTrueAndServiceNameContaining(String name, Pageable pageable);
}