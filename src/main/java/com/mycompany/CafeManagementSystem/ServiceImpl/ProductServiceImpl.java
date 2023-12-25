package com.mycompany.CafeManagementSystem.ServiceImpl;

import com.mycompany.CafeManagementSystem.Entity.Category;
import com.mycompany.CafeManagementSystem.Entity.Product;
import com.mycompany.CafeManagementSystem.JWT.JwtFilter;
import com.mycompany.CafeManagementSystem.Repository.ProductRepository;
import com.mycompany.CafeManagementSystem.Service.IProductService;
import com.mycompany.CafeManagementSystem.Utils.CafeUtils;
import com.mycompany.CafeManagementSystem.Wrapper.ProductWrapper;
import com.mycompany.CafeManagementSystem.constants.CafeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap,false)) {
                    productRepository.save(getProductFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            } else if (!validateId){
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Long.parseLong(requestMap.get("categoryId")));
        Product product = new Product();
        if (isAdd) {
            product.setId(Long.parseLong(requestMap.get("id")));
        } else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Double.parseDouble(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap,true)) {
                    Optional<Product> optional = productRepository.findById(Long.parseLong(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        Product product = getProductFromMap(requestMap,true);
                        product.setStatus(optional.get().getStatus());
                        productRepository.save(product);
                        return CafeUtils.getResponseEntity("Product Updated Successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Product ID does not exist", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productRepository.getAllProduct(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional = productRepository.findById(id);
                if (!optional.isEmpty()){
                    productRepository.deleteById(id);
                    return CafeUtils.getResponseEntity("Product Deleted Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Product ID does not exist", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional = productRepository.findById(Long.parseLong(requestMap.get("id")));
                if (!optional.isEmpty()){
                    productRepository.updateProductStatus(Long.parseLong(requestMap.get("id")), requestMap.get("status"));
                    return CafeUtils.getResponseEntity("Product Status Updated Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Product ID does not exist", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Long id) {
        try {
            return new ResponseEntity<>(productRepository.getProductByCategory(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Long id) {
        try {
            return new ResponseEntity<>(productRepository.getProductById(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
