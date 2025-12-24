package vn.laundryshop.service;

import vn.laundryshop.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import jakarta.validation.Valid;

public interface IUserService {
    User save(User user);
    Optional<User> findByPhone(String phone);
  
    Optional<User> findById(Long id); 
    void delete(Long id);
 // Sửa hàm getAllUsers:
    Page<User> getAllUsers(int pageNo);
	Page<User> searchUsers(String keyword, int page);
	

}