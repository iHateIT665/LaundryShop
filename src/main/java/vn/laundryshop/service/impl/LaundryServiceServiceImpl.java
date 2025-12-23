package vn.laundryshop.service.impl;

import vn.laundryshop.entity.LaundryService;
import vn.laundryshop.repository.ILaundryServiceRepository;
import vn.laundryshop.service.ILaundryServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LaundryServiceServiceImpl implements ILaundryServiceService {

    @Autowired
    private ILaundryServiceRepository serviceRepository;

    @Override
    public List<LaundryService> getAllServices() {
 
        return serviceRepository.findByIsActiveTrue();
    }

    @Override
    public void deleteService(Long id) {
     
     
        Optional<LaundryService> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isPresent()) {
            LaundryService service = serviceOpt.get();
            service.setIsActive(false); // Đánh dấu là đã xóa
            serviceRepository.save(service); // Lưu cập nhật
        }
    }

   
    @Override
    public LaundryService saveService(LaundryService service) {
        if (service.getIsActive() == null) {
            service.setIsActive(true);
        }
        // Trả về đối tượng sau khi lưu (đã có ID)
        return serviceRepository.save(service);
    }

    @Override
    public Optional<LaundryService> findServiceById(Long id) {
        return serviceRepository.findById(id);
    }
}