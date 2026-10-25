package com.surya.optics.service;

import com.surya.optics.dto.CustomerDTO;
import com.surya.optics.entity.Customer;
import com.surya.optics.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    public CustomerService(CustomerRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        return repository.findById(id).map(this::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByPhone(String phone) {
        return repository.findByPhone(phone).map(this::toDto).orElse(null);
    }
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAllCustomers();
        return repository.searchCustomers(keyword.trim()).stream().map(this::toDto).collect(Collectors.toList());
    }
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        validate(dto);
        Customer c = new Customer();
        copy(dto, c);
        c.setCustomerCode("CUST" + System.currentTimeMillis());
        return toDto(repository.save(c));
    }
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        validate(dto);
        Customer c = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        copy(dto, c);
        return toDto(repository.save(c));
    }
    @Transactional
    public void deleteCustomer(Long id) {
        Customer c = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        repository.delete(c);
    }
    private void validate(CustomerDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) throw new IllegalArgumentException("Customer name is required");
        if (dto.getPhone() == null || !dto.getPhone().matches("[0-9]{10}")) throw new IllegalArgumentException("Enter a valid 10 digit phone number");
        if (dto.getAge() != null && (dto.getAge() < 0 || dto.getAge() > 130)) throw new IllegalArgumentException("Age must be between 0 and 130");
    }
    private void copy(CustomerDTO dto, Customer c) {
        c.setName(dto.getName().trim()); c.setPhone(dto.getPhone().trim());
        c.setEmail(blankToNull(dto.getEmail())); c.setAge(dto.getAge());
        c.setAddress(blankToNull(dto.getAddress())); c.setCity(blankToNull(dto.getCity()));
        c.setState(blankToNull(dto.getState())); c.setPincode(blankToNull(dto.getPincode()));
    }
    private String blankToNull(String s) { return s == null || s.trim().isEmpty() ? null : s.trim(); }
    private CustomerDTO toDto(Customer c) {
        CustomerDTO d = new CustomerDTO();
        d.setId(c.getId()); d.setCustomerCode(c.getCustomerCode()); d.setName(c.getName());
        d.setPhone(c.getPhone()); d.setEmail(c.getEmail()); d.setAge(c.getAge());
        d.setAddress(c.getAddress()); d.setCity(c.getCity()); d.setState(c.getState()); d.setPincode(c.getPincode());
        if (c.getCreatedAt() != null) d.setCreatedAt(Date.from(c.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        if (c.getUpdatedAt() != null) d.setUpdatedAt(Date.from(c.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        return d;
    }
}
