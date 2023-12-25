package com.mycompany.CafeManagementSystem.Service;

import com.mycompany.CafeManagementSystem.Entity.Category;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ICategoryService {
    ResponseEntity<List<Category>> getAllCategory(String filterValue);

    ResponseEntity<String> addCategory(Map<String, String> requestMap);

    ResponseEntity<String> updateCategory(Map<String, String> requestMap);

    ResponseEntity<String> deleteCategory( Long id);
}
