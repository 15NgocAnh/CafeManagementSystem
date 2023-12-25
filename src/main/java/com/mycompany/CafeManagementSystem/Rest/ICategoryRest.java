package com.mycompany.CafeManagementSystem.Rest;

import com.mycompany.CafeManagementSystem.Entity.Category;
import com.mycompany.CafeManagementSystem.Entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface ICategoryRest {

    @GetMapping(path = "/get")
    public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue);

    @PostMapping(path = "/add")
    public ResponseEntity<String> addCategory(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/update")
    public ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id);
}
