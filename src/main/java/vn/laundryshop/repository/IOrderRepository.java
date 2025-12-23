package vn.laundryshop.repository;

import vn.laundryshop.entity.Order;
import vn.laundryshop.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByDeliveryStaffOrderByCreatedAtDesc(User staff);
	List<Order> findByCustomerOrderByCreatedAtDesc(User customer);
	
	@Query("SELECT COUNT(o) FROM Order o")
	long countTotalOrders();

	@Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED'")
	Double sumTotalRevenue(); // Chỉ tính tiền những đơn đã hoàn thành

	@Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
	long countPendingOrders();
    List<Order> findAllByOrderByCreatedAtDesc();

 
    @Query("SELECT o FROM Order o WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR o.customer.fullName LIKE %:keyword% OR o.customer.phone LIKE %:keyword%) " +
           "AND (:status IS NULL OR :status = '' OR o.status = :status) " +
           "ORDER BY o.createdAt DESC")
    List<Order> searchOrders(@Param("keyword") String keyword, @Param("status") String status);
}