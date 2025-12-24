package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.ClothingType;
import vn.laundryshop.repository.IClothingTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClothingTypeService {

    private final IClothingTypeRepository typeRepo;



    public ClothingType save(ClothingType type) {
        if (type.getIsActive() == null) {
            type.setIsActive(true);
        }
        return typeRepo.save(type);
    }

    public Optional<ClothingType> findById(Long id) {
        return typeRepo.findById(id);
    }

    public void delete(Long id) {
        Optional<ClothingType> typeOpt = typeRepo.findById(id);
        if (typeOpt.isPresent()) {
            ClothingType type = typeOpt.get();
            type.setIsActive(false); // Xóa mềm
            typeRepo.save(type);
        }
    }
    
    public Page<ClothingType> getTypesWithPaging(int pageNo, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        
        if (keyword != null && !keyword.isEmpty()) {
            return typeRepo.findByIsActiveTrueAndTypeNameContaining(keyword, pageable);
        }
        return typeRepo.findByIsActiveTrue(pageable);
    }

    // Giữ lại hàm này nếu có Controller khác dùng (ví dụ form Add/Edit select box)
    public List<ClothingType> getAllTypes() {
        return typeRepo.findByIsActiveTrue(Pageable.unpaged()).getContent(); 
        // Hoặc dùng findAll nếu repo hỗ trợ, nhưng tạm thời để logic cũ cho an toàn
    }
}