package com.mycompany.CafeManagementSystem.RestImpl;

import com.mycompany.CafeManagementSystem.Rest.IDashboardRest;
import com.mycompany.CafeManagementSystem.Service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestImpl implements IDashboardRest {

    @Autowired
    IDashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}
