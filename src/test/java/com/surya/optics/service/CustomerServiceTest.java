package com.surya.optics.service;

import com.surya.optics.dto.CustomerDTO;
import com.surya.optics.entity.Customer;
import com.surya.optics.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CustomerServiceTest {
    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final CustomerService service = new CustomerService(repository);

    private CustomerDTO validCustomer(Integer age) {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("Test Customer");
        dto.setPhone("9876543210");
        dto.setAge(age);
        return dto;
    }

    @Test
    void rejectsNegativeAgeBeforeDatabaseWrite() {
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(validCustomer(-1)));
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void rejectsAgeOver130BeforeDatabaseWrite() {
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(validCustomer(131)));
        verify(repository, never()).save(any(Customer.class));
    }
}
