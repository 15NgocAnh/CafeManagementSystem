package com.mycompany.CafeManagementSystem.Rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/dashboard")
public interface IDashboardRest {

    @GetMapping(path = "/details")
    public ResponseEntity<Map<String, Object>> getCount();
}
