package com.surya.optics.service;

import com.surya.optics.dto.BillFormDTO;
import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Customer;
import com.surya.optics.repository.BillRepository;
import com.surya.optics.repository.CustomerRepository;
import com.surya.optics.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillServiceTest {
    private final BillRepository bills = mock(BillRepository.class);
    private final CustomerRepository customers = mock(CustomerRepository.class);
    private final PaymentRepository payments = mock(PaymentRepository.class);
    private final BillService service = new BillService(bills, customers, payments);

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
        verify(payments).save(any(com.surya.optics.entity.Payment.class));
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

    @Test
    void recordsPartialPaymentAndClosesInvoiceWhenFullyPaid() {
        Bill bill = new Bill();
        bill.setId(9L); bill.setTotal(new BigDecimal("1000.00"));
        bill.setAdvance(new BigDecimal("200.00")); bill.setBalance(new BigDecimal("800.00"));
        bill.setStatus("PENDING");
        when(bills.findByIdWithCustomer(9L)).thenReturn(Optional.of(bill));
        when(bills.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Bill partial = service.recordPayment(9L, new BigDecimal("300.00"), "UPI");
        assertEquals(new BigDecimal("500.00"), partial.getBalance());
        assertEquals(new BigDecimal("500.00"), partial.getAdvance());
        assertEquals("PENDING", partial.getStatus());
        Bill paid = service.recordPayment(9L, new BigDecimal("500.00"), "Cash");
        assertEquals(BigDecimal.ZERO.setScale(2), paid.getBalance().setScale(2));
        assertEquals("PAID", paid.getStatus());
        assertEquals("Cash", paid.getPaymentMode());
        verify(payments, times(2)).save(any(com.surya.optics.entity.Payment.class));
    }

    @Test
    void rejectsPaymentAboveBalanceAndZeroPayment() {
        Bill bill = new Bill();
        bill.setId(9L); bill.setTotal(new BigDecimal("100.00"));
        bill.setAdvance(new BigDecimal("40.00")); bill.setBalance(new BigDecimal("60.00"));
        when(bills.findByIdWithCustomer(9L)).thenReturn(Optional.of(bill));
        assertThrows(IllegalArgumentException.class, () -> service.recordPayment(9L, new BigDecimal("61.00"), "UPI"));
        assertThrows(IllegalArgumentException.class, () -> service.recordPayment(9L, BigDecimal.ZERO, "UPI"));
    }
}
