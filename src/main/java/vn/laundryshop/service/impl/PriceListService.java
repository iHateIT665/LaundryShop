package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.PriceList;
import vn.laundryshop.repository.IPriceListRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceListService {

    private final IPriceListRepository priceRepo;

  
    public void save(PriceList priceList) {
        if (priceList.getIsActive() == null) {
            priceList.setIsActive(true);
        }
        priceRepo.save(priceList);
    }

    public Optional<PriceList> findById(Long id) {
        return priceRepo.findById(id);
    }

    public void delete(Long id) {
        Optional<PriceList> priceOpt = priceRepo.findById(id);
        if (priceOpt.isPresent()) {
            PriceList p = priceOpt.get();
            p.setIsActive(false); // Xóa mềm
            priceRepo.save(p);
        }
    }
    public Page<PriceList> getPricesWithPaging(int pageNo, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return priceRepo.searchPrices(keyword, pageable);
    }
    
    // ... các hàm cũ giữ nguyên ...
    public List<PriceList> getAllPrices() {
       // Code cũ hoặc gọi repository
       return priceRepo.findAll(); // ví dụ
    }
}