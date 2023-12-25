package com.mycompany.CafeManagementSystem.Repository;

import com.mycompany.CafeManagementSystem.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> getAllBills();

    List<Bill> getAllBillsByUser(@Param("username") String username);
}
