package com.mycompany.CafeManagementSystem.Wrapper;

import lombok.Data;

@Data
public class ProductWrapper {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String status;
    private Long categoryId;
    private String categoryName;

    public ProductWrapper() {

    }

    public ProductWrapper(Long id, String name, String description, Double price, String status, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public ProductWrapper(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductWrapper(Long id, String name, String description, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
