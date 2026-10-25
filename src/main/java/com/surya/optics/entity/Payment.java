package com.surya.optics.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="bill_payments",indexes={@Index(name="idx_payment_bill_date",columnList="bill_id,paid_at")})
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="bill_id",nullable=false) private Bill bill;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal amount;
    @Column(name="payment_mode",length=30) private String paymentMode;
    @Column(name="paid_at",nullable=false) private LocalDateTime paidAt;
    @Column(length=500) private String note;
    @PrePersist protected void onCreate(){if(paidAt==null)paidAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Bill getBill(){return bill;} public void setBill(Bill v){bill=v;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getPaymentMode(){return paymentMode;} public void setPaymentMode(String v){paymentMode=v;}
    public LocalDateTime getPaidAt(){return paidAt;} public void setPaidAt(LocalDateTime v){paidAt=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
}
