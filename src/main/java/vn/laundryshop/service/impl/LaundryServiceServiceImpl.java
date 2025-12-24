package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.LaundryService;
import vn.laundryshop.repository.ILaundryServiceRepository;
import vn.laundryshop.service.ILaundryServiceService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LaundryServiceServiceImpl implements ILaundryServiceService {

    private final ILaundryServiceRepository serviceRepository;

    @Override
    public Page<LaundryService> getAllServices(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        return serviceRepository.findByIsActiveTrue(pageable);
    }
    
    @Override
    public List<LaundryService> getAllServices() {
        return serviceRepository.findByIsActiveTrue();
    }

    @Override
    public Optional<LaundryService> findServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public LaundryService saveService(LaundryService service) {
        // Code cũ của bạn: nếu chưa có active thì set true
        // (Lưu ý: Nếu bạn dùng biến primitive boolean active = true trong Entity thì không cần đoạn if null này nữa)
        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(Long id) {
        Optional<LaundryService> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isPresent()) {
            LaundryService service = serviceOpt.get();
            service.setIsActive(false); // Sửa thành setActive(false) cho khớp với boolean active
            serviceRepository.save(service);
        }
    }

    // --- ĐÂY LÀ ĐOẠN CODE BẠN ĐANG THIẾU ---
    @Override
    public Page<LaundryService> findByIsActiveTrueAndServiceNameContaining(String keyword, Pageable pageable) {
        return serviceRepository.findByIsActiveTrueAndServiceNameContaining(keyword, pageable);
    }
    // ----------------------------------------
}