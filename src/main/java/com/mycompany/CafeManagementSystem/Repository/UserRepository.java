package com.mycompany.CafeManagementSystem.Repository;

import com.mycompany.CafeManagementSystem.Entity.User;
import com.mycompany.CafeManagementSystem.Wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailId(@Param("email") String email);

    List<UserWrapper> getAllUser();
    List<String> getAllAdmin();

    User findByEmail(String email);

    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Long id);

}
