package com.surya.optics.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="bill_items",indexes=@Index(name="idx_bill_items_bill",columnList="bill_id"))
public class BillItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="bill_id",nullable=false) private Bill bill;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id") private Product product;
    @Column(name="product_name",nullable=false,length=120) private String productName;
    @Column(name="product_sku",length=40) private String productSku;
    @Column(nullable=false) private Integer quantity;
    @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
    @Column(name="line_total",nullable=false,precision=12,scale=2) private BigDecimal lineTotal;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Bill getBill(){return bill;} public void setBill(Bill v){bill=v;}
    public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
    public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getProductSku(){return productSku;} public void setProductSku(String v){productSku=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
    public BigDecimal getLineTotal(){return lineTotal;} public void setLineTotal(BigDecimal v){lineTotal=v;}
}
