package vn.laundryshop.repository;

import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    // 1. Tìm đơn hàng theo Staff (Đã gộp chung, thay thế cho deliveryStaff cũ)
    List<Order> findByStaffOrderByCreatedAtDesc(User staff);

    // 2. Tìm đơn hàng của khách hàng (Lịch sử đơn hàng)
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    // 3. Tìm kiếm đơn hàng (Dùng cho Admin) - Đã sửa lỗi cú pháp LIKE
    @Query("SELECT o FROM Order o WHERE " +
           "CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
           "o.customer.fullName LIKE CONCAT('%', :keyword, '%') OR " +
           "o.status LIKE CONCAT('%', :keyword, '%')")
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);
    
    // 4. Các hàm thống kê (Dashboard)
    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED'")
    Double sumTotalRevenue(); 

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPendingOrders();
}