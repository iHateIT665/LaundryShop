package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.ClothingType;
import vn.laundryshop.repository.IClothingTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClothingTypeService {

    private final IClothingTypeRepository typeRepo;

    public List<ClothingType> getAllTypes() {
        return typeRepo.findByIsActiveTrue();
    }

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
}