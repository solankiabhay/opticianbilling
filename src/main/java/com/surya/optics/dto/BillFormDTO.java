package com.surya.optics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class BillFormDTO {
    private Long id;
    private Long customerId;
    @DateTimeFormat(pattern="yyyy-MM-dd") private LocalDate billDate;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private String contactLensName;
    private BigDecimal contactLensCharges = BigDecimal.ZERO;
    private String sunglassName;
    private BigDecimal sunglassCharges = BigDecimal.ZERO;
    private BigDecimal eyeTestingCharges = BigDecimal.ZERO;
    private BigDecimal otherCharges = BigDecimal.ZERO;
    private String glassType;
    private Integer deliveryDays;
    private String paymentMode;
    private BigDecimal advance = BigDecimal.ZERO;
    private String notes;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
    public LocalDate getBillDate(){return billDate;} public void setBillDate(LocalDate v){billDate=v;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
    public String getContactLensName(){return contactLensName;} public void setContactLensName(String v){contactLensName=v;}
    public BigDecimal getContactLensCharges(){return contactLensCharges;} public void setContactLensCharges(BigDecimal v){contactLensCharges=v;}
    public String getSunglassName(){return sunglassName;} public void setSunglassName(String v){sunglassName=v;}
    public BigDecimal getSunglassCharges(){return sunglassCharges;} public void setSunglassCharges(BigDecimal v){sunglassCharges=v;}
    public BigDecimal getEyeTestingCharges(){return eyeTestingCharges;} public void setEyeTestingCharges(BigDecimal v){eyeTestingCharges=v;}
    public BigDecimal getOtherCharges(){return otherCharges;} public void setOtherCharges(BigDecimal v){otherCharges=v;}
    public String getGlassType(){return glassType;} public void setGlassType(String v){glassType=v;}
    public Integer getDeliveryDays(){return deliveryDays;} public void setDeliveryDays(Integer v){deliveryDays=v;}
    public String getPaymentMode(){return paymentMode;} public void setPaymentMode(String v){paymentMode=v;}
    public BigDecimal getAdvance(){return advance;} public void setAdvance(BigDecimal v){advance=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
}
