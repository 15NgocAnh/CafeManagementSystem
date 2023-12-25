package com.mycompany.CafeManagementSystem.Service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IDashboardService {
    ResponseEntity<Map<String, Object>> getCount();
}
