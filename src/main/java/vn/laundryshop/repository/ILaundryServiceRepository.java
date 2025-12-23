package vn.laundryshop.repository;


import vn.laundryshop.entity.LaundryService;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILaundryServiceRepository extends JpaRepository<LaundryService, Long> {
	List<LaundryService> findByIsActiveTrue();
  
}