package com.surya.optics.service;

import com.surya.optics.entity.Bill;
import com.surya.optics.entity.BillItem;
import com.surya.optics.entity.Customer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InvoicePdfServiceTest {
    @Test
    void writesAValidPdfForAnInvoiceWithItems() throws Exception {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setPhone("9876543210");
        Bill bill = new Bill();
        bill.setBillNumber("INV-TEST-123");
        bill.setCustomer(customer);
        bill.setBillDate(LocalDate.of(2026, 10, 25));
        bill.setSubtotal(new BigDecimal("100.00"));
        bill.setContactLensCharges(BigDecimal.ZERO);
        bill.setSunglassCharges(BigDecimal.ZERO);
        bill.setEyeTestingCharges(new BigDecimal("50.00"));
        bill.setOtherCharges(BigDecimal.ZERO);
        bill.setTotal(new BigDecimal("400.00"));
        bill.setAdvance(new BigDecimal("100.00"));
        bill.setBalance(new BigDecimal("300.00"));
        bill.setStatus("PENDING");

        BillItem item = new BillItem();
        item.setProductName("Classic Frame");
        item.setProductSku("FR-001");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("250.00"));
        item.setLineTotal(new BigDecimal("250.00"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new InvoicePdfService().write(bill, Collections.singletonList(item), Collections.emptyList(), output);
        byte[] pdf = output.toByteArray();

        assertTrue(pdf.length > 500, "PDF output should not be empty");
        assertTrue(new String(pdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII).startsWith("%PDF-"));
    }
}
