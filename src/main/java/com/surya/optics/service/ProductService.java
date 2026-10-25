package com.surya.optics.service;

import com.surya.optics.entity.Product;
import com.surya.optics.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository products;
    public ProductService(ProductRepository products){this.products=products;}
    @Transactional(readOnly=true) public List<Product> findAll(){return products.findAllByOrderByNameAsc();}
    @Transactional(readOnly=true) public List<Product> search(String q){return q==null||q.trim().isEmpty()?findAll():products.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrderByNameAsc(q.trim(),q.trim());}
    @Transactional(readOnly=true) public Product find(Long id){return products.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found"));}
    @Transactional public Product save(Product form){
        validate(form);
        Product p;
        if(form.getId()==null){p=form;}else{p=find(form.getId());p.setName(form.getName().trim());p.setSku(form.getSku().trim().toUpperCase());p.setCategory(form.getCategory());p.setBrand(form.getBrand());p.setPurchasePrice(form.getPurchasePrice());p.setSellingPrice(form.getSellingPrice());p.setStockQuantity(form.getStockQuantity());p.setReorderLevel(form.getReorderLevel());p.setDescription(form.getDescription());p.setActive(form.isActive());}
        p.setName(p.getName().trim());p.setSku(p.getSku().trim().toUpperCase());
        return products.save(p);
    }
    @Transactional public Product adjustStock(Long id,Integer delta){
        if(delta==null||delta==0)throw new IllegalArgumentException("Stock adjustment cannot be zero");
        Product p=find(id);long next=(long)p.getStockQuantity()+delta;
        if(next<0||next>Integer.MAX_VALUE)throw new IllegalArgumentException("Stock cannot become negative or exceed supported quantity");
        p.setStockQuantity((int)next);return products.save(p);
    }
    @Transactional public void deactivate(Long id){Product p=find(id);p.setActive(false);products.save(p);}
    @Transactional(readOnly=true) public long lowStockCount(){return products.findAllByOrderByNameAsc().stream().filter(Product::isActive).filter(p->p.getStockQuantity()<=p.getReorderLevel()).count();}
    private void validate(Product p){
        if(p.getName()==null||p.getName().trim().isEmpty())throw new IllegalArgumentException("Product name is required");
        if(p.getSku()==null||p.getSku().trim().isEmpty())throw new IllegalArgumentException("SKU is required");
        if(p.getName().trim().length()>120||p.getSku().trim().length()>40)throw new IllegalArgumentException("Name or SKU is too long");
        if(p.getPurchasePrice()==null||p.getPurchasePrice().compareTo(BigDecimal.ZERO)<0)throw new IllegalArgumentException("Purchase price cannot be negative");
        if(p.getSellingPrice()==null||p.getSellingPrice().compareTo(BigDecimal.ZERO)<0)throw new IllegalArgumentException("Selling price cannot be negative");
        if(p.getStockQuantity()==null||p.getStockQuantity()<0)throw new IllegalArgumentException("Stock quantity cannot be negative");
        if(p.getReorderLevel()==null||p.getReorderLevel()<0)throw new IllegalArgumentException("Reorder level cannot be negative");
        boolean duplicate=p.getId()==null?products.existsBySkuIgnoreCase(p.getSku().trim()):products.existsBySkuIgnoreCaseAndIdNot(p.getSku().trim(),p.getId());
        if(duplicate)throw new IllegalArgumentException("SKU already exists; choose a unique SKU");
    }
}
