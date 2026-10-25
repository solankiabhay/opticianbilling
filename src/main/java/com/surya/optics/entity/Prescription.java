package com.surya.optics.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private Bill bill;
    @Column(name = "right_eye_sph") private BigDecimal rightEyeSph;
    @Column(name = "right_eye_cyl") private BigDecimal rightEyeCyl;
    @Column(name = "right_eye_axis") private Integer rightEyeAxis;
    @Column(name = "right_eye_vn", length = 20) private String rightEyeVn;
    @Column(name = "left_eye_sph") private BigDecimal leftEyeSph;
    @Column(name = "left_eye_cyl") private BigDecimal leftEyeCyl;
    @Column(name = "left_eye_axis") private Integer leftEyeAxis;
    @Column(name = "left_eye_vn", length = 20) private String leftEyeVn;
    private BigDecimal addition;
    @Column(name = "glass_type", length = 50) private String glassType;
    @Column(name = "prescription_date") private LocalDate prescriptionDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column(columnDefinition = "TEXT") private String notes;

    @PrePersist protected void onCreate() {
        if (prescriptionDate == null) prescriptionDate = LocalDate.now();
        if (expiryDate == null && prescriptionDate != null) expiryDate = prescriptionDate.plusYears(2);
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
    public BigDecimal getRightEyeSph() { return rightEyeSph; }
    public void setRightEyeSph(BigDecimal value) { this.rightEyeSph = value; }
    public BigDecimal getRightEyeCyl() { return rightEyeCyl; }
    public void setRightEyeCyl(BigDecimal value) { this.rightEyeCyl = value; }
    public Integer getRightEyeAxis() { return rightEyeAxis; }
    public void setRightEyeAxis(Integer value) { this.rightEyeAxis = value; }
    public String getRightEyeVn() { return rightEyeVn; }
    public void setRightEyeVn(String value) { this.rightEyeVn = value; }
    public BigDecimal getLeftEyeSph() { return leftEyeSph; }
    public void setLeftEyeSph(BigDecimal value) { this.leftEyeSph = value; }
    public BigDecimal getLeftEyeCyl() { return leftEyeCyl; }
    public void setLeftEyeCyl(BigDecimal value) { this.leftEyeCyl = value; }
    public Integer getLeftEyeAxis() { return leftEyeAxis; }
    public void setLeftEyeAxis(Integer value) { this.leftEyeAxis = value; }
    public String getLeftEyeVn() { return leftEyeVn; }
    public void setLeftEyeVn(String value) { this.leftEyeVn = value; }
    public BigDecimal getAddition() { return addition; }
    public void setAddition(BigDecimal value) { this.addition = value; }
    public String getGlassType() { return glassType; }
    public void setGlassType(String value) { this.glassType = value; }
    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate value) { this.prescriptionDate = value; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate value) { this.expiryDate = value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { this.notes = value; }
}
