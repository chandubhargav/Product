package com.retail.ProductApplication.Repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retail.ProductApplication.Model.ApprovalQueue;
import com.retail.ProductApplication.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrueOrderByPostedDateDesc();
    
    List<Product> findByProductName(String productName);
    
    List<Product> findByPriceBetween(int minPrice, int maxPrice);
    
    List<Product> findByPostedDateBetween(Date minPrice, Date maxPrice);
    
    List<Product> findAllByOrderByPostedDateAsc();
    


//    @Query("update products p set c.name = :name WHERE c.id = :customerId")
//    void setCustomerName(@Param("customerId") Long id, @Param("name") String name);
}