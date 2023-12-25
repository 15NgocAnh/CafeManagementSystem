package com.mycompany.CafeManagementSystem.Rest;

import com.mycompany.CafeManagementSystem.Entity.Product;
import com.mycompany.CafeManagementSystem.Wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/product")
public interface IProductRest {

    @GetMapping("/get")
    ResponseEntity<List<ProductWrapper>> getAllProduct();
    @PostMapping("/add")
    ResponseEntity<String> addProduct(@RequestBody Map<String, String> requestMap);
    @PostMapping("/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);
    @PostMapping("/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Long id);
    @PostMapping("updateStatus")
    ResponseEntity<String> updateProductStatus(@RequestBody Map<String, String> requestMap);
    @GetMapping(path = "/getByCategory/{id}")
    public ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable Long id);
    @GetMapping(path = "getById/{id}")
    public ResponseEntity<ProductWrapper> getById(@PathVariable Long id);
}
