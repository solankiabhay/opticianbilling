package com.surya.optics.repository;

import com.surya.optics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByOrderByNameAsc();
    List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrderByNameAsc(String name,String sku);
    Optional<Product> findBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCaseAndIdNot(String sku,Long id);
    long countByStockQuantityLessThanEqualAndActiveTrue(Integer threshold);
}
