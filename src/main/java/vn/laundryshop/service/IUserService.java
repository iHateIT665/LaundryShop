package vn.laundryshop.service;

import vn.laundryshop.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User save(User user);
    Optional<User> findByPhone(String phone);
    List<User> getAllUsers();
    Optional<User> findById(Long id); 
    void delete(Long id);
}