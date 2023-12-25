package com.mycompany.CafeManagementSystem.Service;

import com.mycompany.CafeManagementSystem.Entity.Product;
import com.mycompany.CafeManagementSystem.Wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IProductService {

    ResponseEntity<String> addProduct(Map<String, String> requestMap);

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProduct();

    ResponseEntity<String> deleteProduct(Long id);

    ResponseEntity<String> updateProductStatus(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getByCategory(Long id);

    ResponseEntity<ProductWrapper> getById(Long id);
}
