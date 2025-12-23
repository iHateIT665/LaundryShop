package vn.laundryshop.repository;

import vn.laundryshop.entity.ClothingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IClothingTypeRepository extends JpaRepository<ClothingType, Long> {
  
    List<ClothingType> findByIsActiveTrue();
}