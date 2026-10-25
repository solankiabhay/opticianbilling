package com.surya.optics.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bills")
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bill_number", unique = true, nullable = false, length = 20)
    private String billNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(name = "bill_date") private LocalDate billDate;
    @Column(name = "bill_time") private LocalTime billTime;
    private BigDecimal subtotal;
    @Column(nullable = false) private BigDecimal total;
    private BigDecimal advance = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(name = "contact_lens_name", length = 100) private String contactLensName;
    @Column(name = "contact_lens_charges") private BigDecimal contactLensCharges = BigDecimal.ZERO;
    @Column(name = "sunglass_name", length = 100) private String sunglassName;
    @Column(name = "sunglass_charges") private BigDecimal sunglassCharges = BigDecimal.ZERO;
    @Column(name = "eye_testing_charges") private BigDecimal eyeTestingCharges = BigDecimal.ZERO;
    @Column(name = "other_charges") private BigDecimal otherCharges = BigDecimal.ZERO;
    @Column(name = "glass_type", length = 50) private String glassType;
    @Column(name = "delivery_days") private Integer deliveryDays;
    @Column(name = "delivery_date") private LocalDate deliveryDate;
    @Column(length = 20) private String status = "PENDING";
    @Column(name = "payment_mode", length = 20) private String paymentMode;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @OneToOne(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prescription prescription;

    @PrePersist protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (billTime == null) billTime = LocalTime.now();
        if (status == null) status = "PENDING";
        if (advance == null) advance = BigDecimal.ZERO;
        if (balance == null) balance = BigDecimal.ZERO;
        if (contactLensCharges == null) contactLensCharges = BigDecimal.ZERO;
        if (sunglassCharges == null) sunglassCharges = BigDecimal.ZERO;
        if (eyeTestingCharges == null) eyeTestingCharges = BigDecimal.ZERO;
        if (otherCharges == null) otherCharges = BigDecimal.ZERO;
    }
    public Long getId() { return id; }
    public void setId(Long v) { id = v; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String v) { billNumber = v; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer v) { customer = v; }
    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate v) { billDate = v; }
    public LocalTime getBillTime() { return billTime; }
    public void setBillTime(LocalTime v) { billTime = v; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal v) { subtotal = v; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { total = v; }
    public BigDecimal getAdvance() { return advance; }
    public void setAdvance(BigDecimal v) { advance = v; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal v) { balance = v; }
    public String getContactLensName() { return contactLensName; }
    public void setContactLensName(String v) { contactLensName = v; }
    public BigDecimal getContactLensCharges() { return contactLensCharges; }
    public void setContactLensCharges(BigDecimal v) { contactLensCharges = v; }
    public String getSunglassName() { return sunglassName; }
    public void setSunglassName(String v) { sunglassName = v; }
    public BigDecimal getSunglassCharges() { return sunglassCharges; }
    public void setSunglassCharges(BigDecimal v) { sunglassCharges = v; }
    public BigDecimal getEyeTestingCharges() { return eyeTestingCharges; }
    public void setEyeTestingCharges(BigDecimal v) { eyeTestingCharges = v; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal v) { otherCharges = v; }
    public String getGlassType() { return glassType; }
    public void setGlassType(String v) { glassType = v; }
    public Integer getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(Integer v) { deliveryDays = v; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate v) { deliveryDate = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String v) { paymentMode = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }
    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription v) { prescription = v; }
}
