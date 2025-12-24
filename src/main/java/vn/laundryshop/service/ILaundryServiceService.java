package vn.laundryshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.laundryshop.entity.LaundryService;

import java.util.List;
import java.util.Optional;

public interface ILaundryServiceService {
    
    List<LaundryService> getAllServices();
    
    Page<LaundryService> getAllServices(int pageNo);
    
    Optional<LaundryService> findServiceById(Long id);
    
    LaundryService saveService(LaundryService service);
    
    void deleteService(Long id);

    // --- HÀM TÌM KIẾM CẦN IMPLEMENT ---
    // Lưu ý: Tham số thứ 2 là Pageable
    Page<LaundryService> findByIsActiveTrueAndServiceNameContaining(String keyword, Pageable pageable);
}