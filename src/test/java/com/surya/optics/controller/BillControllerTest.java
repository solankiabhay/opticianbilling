package com.surya.optics.controller;

import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Customer;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.mock.web.MockHttpServletResponse;
import com.surya.optics.service.BillService;
import com.surya.optics.service.CustomerService;
import com.surya.optics.service.ProductService;
import com.surya.optics.service.InvoicePdfService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.io.OutputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BillControllerTest {
    @Test
    void paymentValidationErrorKeepsInvoiceCollectionsInModel() throws Exception {
        BillService bills = mock(BillService.class);
        CustomerService customers = mock(CustomerService.class);
        ProductService products = mock(ProductService.class);
        InvoicePdfService pdfs = mock(InvoicePdfService.class);
        Bill bill = new Bill();
        bill.setId(7L);
        bill.setTotal(new BigDecimal("100.00"));
        bill.setAdvance(new BigDecimal("40.00"));
        bill.setBalance(new BigDecimal("60.00"));
        when(bills.recordPayment(7L, new BigDecimal("61.00"), "UPI"))
                .thenThrow(new IllegalArgumentException("Payment cannot exceed outstanding balance"));
        when(bills.findById(7L)).thenReturn(bill);
        when(bills.findPayments(7L)).thenReturn(Collections.emptyList());
        when(bills.findItems(7L)).thenReturn(Collections.emptyList());

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new BillController(bills, customers, products, pdfs)).build();
        mvc.perform(post("/bills/payment/7").param("amount", "61.00").param("paymentMode", "UPI"))
                .andExpect(status().isOk())
                .andExpect(view().name("bills/view"))
                .andExpect(model().attributeExists("bill", "payments", "billItems", "paymentError"));

        verify(bills).findPayments(7L);
        verify(bills).findItems(7L);
    }
    @Test
    void csvExportNeutralizesFormulaInjectionAfterLeadingWhitespace() throws Exception {
        BillService bills = mock(BillService.class);
        CustomerService customers = mock(CustomerService.class);
        ProductService products = mock(ProductService.class);
        InvoicePdfService pdfs = mock(InvoicePdfService.class);
        Customer customer = new Customer();
        customer.setName("   =HYPERLINK(\"https://attacker.example\")");
        customer.setPhone("+123456789");
        Bill bill = new Bill();
        bill.setBillNumber("INV-1");
        bill.setCustomer(customer);
        bill.setBillDate(LocalDate.of(2026, 10, 25));
        bill.setTotal(new BigDecimal("100.00"));
        bill.setAdvance(new BigDecimal("20.00"));
        bill.setBalance(new BigDecimal("80.00"));
        bill.setStatus("PENDING");
        bill.setPaymentMode("UPI");
        when(bills.findAll()).thenReturn(Collections.singletonList(bill));

        MockHttpServletResponse response = new MockHttpServletResponse();
        new BillController(bills, customers, products, pdfs).exportCsv(response);
        String csv = response.getContentAsString();

        assertTrue(csv.contains("\"'   =HYPERLINK(\"\"https://attacker.example\"\")\""));
        assertTrue(csv.contains("\"'+123456789\""));
    }

    @Test
    void pdfDownloadReturnsAttachmentAndDelegatesInvoiceData() throws Exception {
        BillService bills = mock(BillService.class);
        CustomerService customers = mock(CustomerService.class);
        ProductService products = mock(ProductService.class);
        InvoicePdfService pdfs = mock(InvoicePdfService.class);
        Bill bill = new Bill();
        bill.setId(7L);
        bill.setBillNumber("INV-TEST-7");
        when(bills.findById(7L)).thenReturn(bill);
        when(bills.findItems(7L)).thenReturn(Collections.emptyList());
        when(bills.findPayments(7L)).thenReturn(Collections.emptyList());

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new BillController(bills, customers, products, pdfs)).build();
        mvc.perform(get("/bills/pdf/7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=invoice-INV-TEST-7.pdf"));

        verify(pdfs).write(eq(bill), anyList(), anyList(), any(OutputStream.class));
    }

    @Test
    void pdfEndpointSetsDownloadHeadersAndDelegatesPdfRendering() throws Exception {
        BillService bills = mock(BillService.class);
        CustomerService customers = mock(CustomerService.class);
        ProductService products = mock(ProductService.class);
        InvoicePdfService pdfs = mock(InvoicePdfService.class);
        Bill bill = new Bill();
        bill.setId(42L);
        bill.setBillNumber("INV-TEST-42");
        when(bills.findById(42L)).thenReturn(bill);
        when(bills.findItems(42L)).thenReturn(Collections.emptyList());
        when(bills.findPayments(42L)).thenReturn(Collections.emptyList());

        MockHttpServletResponse response = new MockHttpServletResponse();
        new BillController(bills, customers, products, pdfs).exportPdf(42L, response);

        assertEquals("application/pdf", response.getContentType());
        assertEquals("attachment; filename=invoice-INV-TEST-42.pdf", response.getHeader("Content-Disposition"));
        verify(pdfs).write(eq(bill), anyList(), anyList(), any(java.io.OutputStream.class));
    }

}
