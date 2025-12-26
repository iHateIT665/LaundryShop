package vn.laundryshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.laundryshop.entity.Complaint;
import vn.laundryshop.entity.User;

import java.util.List;

@Repository
public interface IComplaintRepository extends JpaRepository<Complaint, Long> {
    // Lấy lịch sử khiếu nại của 1 khách hàng
    List<Complaint> findByUserOrderByCreatedAtDesc(User user);
    
    // Lấy tất cả khiếu nại cho Admin (Mới nhất lên đầu)
    List<Complaint> findAllByOrderByCreatedAtDesc();
}