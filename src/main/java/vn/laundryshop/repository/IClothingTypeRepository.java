package vn.laundryshop.repository;

import vn.laundryshop.entity.ClothingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClothingTypeRepository extends JpaRepository<ClothingType, Long> {
    // Lấy tất cả có phân trang
    Page<ClothingType> findByIsActiveTrue(Pageable pageable);

    // Tìm kiếm có phân trang
    Page<ClothingType> findByIsActiveTrueAndTypeNameContaining(String name, Pageable pageable);
}