package com.surya.optics.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="products", uniqueConstraints=@UniqueConstraint(name="uk_product_sku",columnNames="sku"))
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=40,unique=true) private String sku;
    @Column(length=60) private String category;
    @Column(length=80) private String brand;
    @Column(name="purchase_price",nullable=false,precision=12,scale=2) private BigDecimal purchasePrice=BigDecimal.ZERO;
    @Column(name="selling_price",nullable=false,precision=12,scale=2) private BigDecimal sellingPrice=BigDecimal.ZERO;
    @Column(name="stock_quantity",nullable=false) private Integer stockQuantity=0;
    @Column(name="reorder_level",nullable=false) private Integer reorderLevel=5;
    @Column(length=1000) private String description;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
    @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt;
    @PrePersist protected void onCreate(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(stockQuantity==null)stockQuantity=0;if(reorderLevel==null)reorderLevel=5;if(purchasePrice==null)purchasePrice=BigDecimal.ZERO;if(sellingPrice==null)sellingPrice=BigDecimal.ZERO;}
    @PreUpdate protected void onUpdate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getSku(){return sku;} public void setSku(String v){sku=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getBrand(){return brand;} public void setBrand(String v){brand=v;}
    public BigDecimal getPurchasePrice(){return purchasePrice;} public void setPurchasePrice(BigDecimal v){purchasePrice=v;}
    public BigDecimal getSellingPrice(){return sellingPrice;} public void setSellingPrice(BigDecimal v){sellingPrice=v;}
    public Integer getStockQuantity(){return stockQuantity;} public void setStockQuantity(Integer v){stockQuantity=v;}
    public Integer getReorderLevel(){return reorderLevel;} public void setReorderLevel(Integer v){reorderLevel=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
