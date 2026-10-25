package com.surya.optics.service;

import com.surya.optics.dto.BillFormDTO;
import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Customer;
import com.surya.optics.repository.BillRepository;
import com.surya.optics.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillServiceTest {
    private final BillRepository bills = mock(BillRepository.class);
    private final CustomerRepository customers = mock(CustomerRepository.class);
    private final BillService service = new BillService(bills, customers);

    @Test
    void calculatesTotalAndOutstandingBalance() {
        when(customers.findById(5L)).thenReturn(Optional.of(new Customer()));
        when(bills.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BillFormDTO dto = new BillFormDTO();
        dto.setCustomerId(5L);
        dto.setSubtotal(new BigDecimal("1000.00"));
        dto.setContactLensCharges(new BigDecimal("150.00"));
        dto.setEyeTestingCharges(new BigDecimal("50.00"));
        dto.setAdvance(new BigDecimal("500.00"));
        Bill saved = service.create(dto);
        assertEquals(new BigDecimal("1200.00"), saved.getTotal());
        assertEquals(new BigDecimal("700.00"), saved.getBalance());
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    void rejectsAdvanceGreaterThanTotal() {
        when(customers.findById(5L)).thenReturn(Optional.of(new Customer()));
        BillFormDTO dto = new BillFormDTO();
        dto.setCustomerId(5L);
        dto.setSubtotal(new BigDecimal("100.00"));
        dto.setAdvance(new BigDecimal("101.00"));
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }
}
