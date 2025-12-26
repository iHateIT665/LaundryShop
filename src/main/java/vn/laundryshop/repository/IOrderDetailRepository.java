package vn.laundryshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.laundryshop.dto.StatsDTO;
import vn.laundryshop.entity.OrderDetail;

import java.util.List;

public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // 3. Top 5 Dịch vụ được sử dụng nhiều nhất
    // Logic: Join OrderDetail -> PriceList -> LaundryService
    @Query("SELECT new vn.laundryshop.dto.StatsDTO(s.serviceName, COUNT(od)) " +
           "FROM OrderDetail od " +
           "JOIN od.priceList p " +
           "JOIN p.service s " +
           "GROUP BY s.serviceName " +
           "ORDER BY COUNT(od) DESC")
    List<StatsDTO> getTopServices();
}