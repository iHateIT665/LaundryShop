package vn.laundryshop.service;

import vn.laundryshop.entity.LaundryService;
import java.util.List;
import java.util.Optional;

public interface ILaundryServiceService {
    List<LaundryService> getAllServices();
    LaundryService saveService(LaundryService service);
    void deleteService(Long id);
    Optional<LaundryService> findServiceById(Long id);
}