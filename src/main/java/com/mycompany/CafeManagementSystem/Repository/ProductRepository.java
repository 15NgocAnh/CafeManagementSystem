package com.mycompany.CafeManagementSystem.Repository;

import com.mycompany.CafeManagementSystem.Entity.Product;
import com.mycompany.CafeManagementSystem.Wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<ProductWrapper> getAllProduct();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("id") Long id, @Param("status") String status);

    List<ProductWrapper> getProductByCategory(@Param("id") Long id);

    ProductWrapper getProductById(@Param("id") Long id);
}
