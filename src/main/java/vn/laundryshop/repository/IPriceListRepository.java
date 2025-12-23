package vn.laundryshop.repository;

import vn.laundryshop.entity.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IPriceListRepository extends JpaRepository<PriceList, Long> {
    List<PriceList> findByIsActiveTrue();

    // 🔍 TÌM KIẾM GIÁ:
    @Query("SELECT p FROM PriceList p WHERE p.isActive = true AND " +
           "(:keyword IS NULL OR :keyword = '' OR p.service.serviceName LIKE %:keyword% OR p.clothingType.typeName LIKE %:keyword%)")
    List<PriceList> searchPrices(@Param("keyword") String keyword);
}