package com.surya.optics.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PrescriptionDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long billId;
    private String billNumber;
    private BigDecimal rightSph;
    private BigDecimal rightCyl;
    private Integer rightAxis;
    private String rightVn;
    private BigDecimal leftSph;
    private BigDecimal leftCyl;
    private Integer leftAxis;
    private String leftVn;
    private BigDecimal addition;
    private String glassType;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate prescriptionDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String notes;

    private String formatPower(BigDecimal value) {
        if (value == null) return "-";
        BigDecimal normalized = value.stripTrailingZeros();
        String text = normalized.toPlainString();
        return value.signum() > 0 ? "+" + text : text;
    }
    private String formatPowerInput(BigDecimal value) {
        if (value == null) return "";
        String text = value.stripTrailingZeros().toPlainString();
        return value.signum() > 0 ? "+" + text : text;
    }
    public String getRightSphInput() { return formatPowerInput(rightSph); }
    public String getRightCylInput() { return formatPowerInput(rightCyl); }
    public String getLeftSphInput() { return formatPowerInput(leftSph); }
    public String getLeftCylInput() { return formatPowerInput(leftCyl); }
    public String getAdditionInput() { return formatPowerInput(addition); }
    public String getRightSphDisplay() { return formatPower(rightSph); }
    public String getRightCylDisplay() { return formatPower(rightCyl); }
    public String getLeftSphDisplay() { return formatPower(leftSph); }
    public String getLeftCylDisplay() { return formatPower(leftCyl); }
    public String getAdditionDisplay() { return formatPower(addition); }

    public PrescriptionDTO() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public BigDecimal getRightSph() { return rightSph; }
    public void setRightSph(BigDecimal rightSph) { this.rightSph = rightSph; }
    public BigDecimal getRightCyl() { return rightCyl; }
    public void setRightCyl(BigDecimal rightCyl) { this.rightCyl = rightCyl; }
    public Integer getRightAxis() { return rightAxis; }
    public void setRightAxis(Integer rightAxis) { this.rightAxis = rightAxis; }
    public String getRightVn() { return rightVn; }
    public void setRightVn(String rightVn) { this.rightVn = rightVn; }
    public BigDecimal getLeftSph() { return leftSph; }
    public void setLeftSph(BigDecimal leftSph) { this.leftSph = leftSph; }
    public BigDecimal getLeftCyl() { return leftCyl; }
    public void setLeftCyl(BigDecimal leftCyl) { this.leftCyl = leftCyl; }
    public Integer getLeftAxis() { return leftAxis; }
    public void setLeftAxis(Integer leftAxis) { this.leftAxis = leftAxis; }
    public String getLeftVn() { return leftVn; }
    public void setLeftVn(String leftVn) { this.leftVn = leftVn; }
    public BigDecimal getAddition() { return addition; }
    public void setAddition(BigDecimal addition) { this.addition = addition; }
    public String getGlassType() { return glassType; }
    public void setGlassType(String glassType) { this.glassType = glassType; }
    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
