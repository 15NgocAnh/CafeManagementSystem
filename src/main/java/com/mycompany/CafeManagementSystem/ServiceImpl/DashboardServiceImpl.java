package com.mycompany.CafeManagementSystem.ServiceImpl;

import com.mycompany.CafeManagementSystem.Repository.BillRepository;
import com.mycompany.CafeManagementSystem.Repository.CategoryRepository;
import com.mycompany.CafeManagementSystem.Repository.ProductRepository;
import com.mycompany.CafeManagementSystem.Service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    BillRepository billRepository;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("category", categoryRepository.count());
        map.put("product", productRepository.count());
        map.put("bill", billRepository.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
