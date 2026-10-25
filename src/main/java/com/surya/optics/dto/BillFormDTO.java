package com.surya.optics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class BillFormDTO {
    private Long id;
    private Long customerId;
    private Long productId;
    private Integer quantity = 1;
    private List<Long> productIds = new ArrayList<>();
    private List<Integer> quantities = new ArrayList<>();
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
    public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public List<Long> getProductIds(){return productIds;} public void setProductIds(List<Long> v){productIds=v==null?new ArrayList<>():v;}
    public List<Integer> getQuantities(){return quantities;} public void setQuantities(List<Integer> v){quantities=v==null?new ArrayList<>():v;}
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
