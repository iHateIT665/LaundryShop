package vn.laundryshop.repository;

import vn.laundryshop.entity.PriceList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPriceListRepository extends JpaRepository<PriceList, Long> {
    
    // --- SỬA LẠI ĐOẠN QUERY NÀY ---
    // Thay %:keyword% thành CONCAT('%', :keyword, '%')
    @Query("SELECT p FROM PriceList p WHERE p.isActive = true AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "p.service.serviceName LIKE CONCAT('%', :keyword, '%') OR " +
           "p.clothingType.typeName LIKE CONCAT('%', :keyword, '%'))")
    Page<PriceList> searchPrices(@Param("keyword") String keyword, Pageable pageable);

    // Lấy tất cả + Phân trang
    Page<PriceList> findByIsActiveTrue(Pageable pageable);
}