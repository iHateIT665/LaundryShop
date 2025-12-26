package vn.laundryshop.repository;

import vn.laundryshop.dto.StatsDTO;
import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    // 1. Tìm đơn hàng theo Staff
    List<Order> findByStaffOrderByCreatedAtDesc(User staff);

    // 2. Tìm đơn hàng của khách hàng
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    // 3. Tìm kiếm đơn hàng
    @Query("SELECT o FROM Order o WHERE " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(o.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.status) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);
    
    // 4. Các hàm thống kê cơ bản
    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED'")
    Double sumTotalRevenue(); 

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPendingOrders();
    
    // 5. Top Dịch vụ (Giữ nguyên JPQL vì hàm này chạy tốt)
    @Query("SELECT new vn.laundryshop.dto.StatsDTO(od.priceList.service.serviceName, COUNT(od)) " +
           "FROM OrderDetail od " +
           "GROUP BY od.priceList.service.serviceName " +
           "ORDER BY COUNT(od) DESC")
    List<StatsDTO> getTopServices(Pageable pageable);

    // 6. Doanh thu 7 ngày gần nhất (SỬA: Nhận tham số startDate từ Java)
    @Query("SELECT new vn.laundryshop.dto.StatsDTO(FUNCTION('DATE_FORMAT', o.createdAt, '%d/%m'), SUM(o.totalAmount)) " +
            "FROM Order o " +
            "WHERE o.status = 'COMPLETED' AND o.createdAt >= :startDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%d/%m') " +
            "ORDER BY MAX(o.createdAt) ASC")
     List<StatsDTO> getRevenueLast7Days(@Param("startDate") LocalDateTime startDate);

    // 7. Thống kê doanh thu theo tháng (MỚI: Gọi Stored Procedure)
    // Trả về Object[] để tránh lỗi mapping của Hibernate
    @Query(value = "CALL GetMonthlyRevenue()", nativeQuery = true)
    List<Object[]> getRevenueByMonthNative();
}