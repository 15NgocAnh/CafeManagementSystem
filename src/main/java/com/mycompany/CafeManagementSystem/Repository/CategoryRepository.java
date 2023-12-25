package com.mycompany.CafeManagementSystem.Repository;

import com.mycompany.CafeManagementSystem.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> getAllCategory();

}
