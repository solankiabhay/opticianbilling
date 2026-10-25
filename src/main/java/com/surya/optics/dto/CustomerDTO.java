package com.surya.optics.dto;

import java.util.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

public class CustomerDTO {
    private Long id;
    private String customerCode;
    private String name;
    private String phone;
    private String email;
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 120, message = "Age must be 120 or below")
    private Integer age;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private Date createdAt;
    private Date updatedAt;
    private Integer totalBills;
    private Double totalSpent;

    public CustomerDTO() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Integer getTotalBills() { return totalBills; }
    public void setTotalBills(Integer totalBills) { this.totalBills = totalBills; }
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
