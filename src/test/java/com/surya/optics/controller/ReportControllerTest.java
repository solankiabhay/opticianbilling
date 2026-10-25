package com.surya.optics.controller;

import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Payment;
import com.surya.optics.repository.PaymentRepository;
import com.surya.optics.service.BillService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {
    @Test
    void receivedTotalUsesReceiptDateRatherThanInvoiceAdvance() {
        BillService bills = mock(BillService.class);
        PaymentRepository payments = mock(PaymentRepository.class);
        LocalDate from = LocalDate.of(2026, 10, 1);
        LocalDate to = LocalDate.of(2026, 10, 31);

        Bill invoice = new Bill();
        invoice.setBillDate(LocalDate.of(2026, 10, 5));
        invoice.setTotal(new BigDecimal("1000.00"));
        invoice.setAdvance(new BigDecimal("600.00"));
        invoice.setBalance(new BigDecimal("400.00"));
        when(bills.findAll()).thenReturn(Collections.singletonList(invoice));
        Payment receipt = new Payment();
        receipt.setAmount(new BigDecimal("200.00"));
        receipt.setPaidAt(LocalDateTime.of(2026, 10, 10, 12, 0));
        when(payments.findByPaidAtGreaterThanEqualAndPaidAtLessThanOrderByPaidAtAsc(
                from.atStartOfDay(), to.plusDays(1).atStartOfDay())).thenReturn(Arrays.asList(receipt));

        ExtendedModelMap model = new ExtendedModelMap();
        String view = new ReportController(bills, payments).index(from, to, model);

        assertEquals("reports/index", view);
        assertEquals(new BigDecimal("1000.00"), model.get("salesTotal"));
        assertEquals(new BigDecimal("200.00"), model.get("receivedTotal"));
        assertEquals(new BigDecimal("400.00"), model.get("outstandingTotal"));
        verify(payments).findByPaidAtGreaterThanEqualAndPaidAtLessThanOrderByPaidAtAsc(
                from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    @Test
    void invalidDateRangeFallsBackToStartOfEndMonthAndShowsError() {
        BillService bills = mock(BillService.class);
        PaymentRepository payments = mock(PaymentRepository.class);
        when(bills.findAll()).thenReturn(Collections.emptyList());
        when(payments.findByPaidAtGreaterThanEqualAndPaidAtLessThanOrderByPaidAtAsc(any(), any()))
                .thenReturn(Collections.emptyList());
        ExtendedModelMap model = new ExtendedModelMap();

        new ReportController(bills, payments).index(
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 1), model);

        assertEquals("Start date cannot be after end date.", model.get("error"));
        assertEquals(LocalDate.of(2026, 10, 1), model.get("start"));
        assertEquals(LocalDate.of(2026, 10, 1), model.get("end"));
    }
}
