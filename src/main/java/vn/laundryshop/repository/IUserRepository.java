package vn.laundryshop.repository;

import vn.laundryshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    

    long countByRole(String role);
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(:keyword IS NULL OR :keyword = '' OR u.fullName LIKE CONCAT('%', :keyword, '%') OR u.phone LIKE CONCAT('%', :keyword, '%') OR u.address LIKE CONCAT('%', :keyword, '%')) " +
           "AND (:role IS NULL OR :role = '' OR u.role = :role)")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") String role);

	List<User> findByIsActiveTrue();
}