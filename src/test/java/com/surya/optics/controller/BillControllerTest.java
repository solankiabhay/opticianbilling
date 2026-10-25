package com.surya.optics.controller;

import com.surya.optics.entity.Bill;
import com.surya.optics.service.BillService;
import com.surya.optics.service.CustomerService;
import com.surya.optics.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import java.math.BigDecimal;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BillControllerTest {
    @Test
    void paymentValidationErrorKeepsInvoiceCollectionsInModel() throws Exception {
        BillService bills = mock(BillService.class);
        CustomerService customers = mock(CustomerService.class);
        ProductService products = mock(ProductService.class);
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

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new BillController(bills, customers, products)).build();
        mvc.perform(post("/bills/payment/7").param("amount", "61.00").param("paymentMode", "UPI"))
                .andExpect(status().isOk())
                .andExpect(view().name("bills/view"))
                .andExpect(model().attributeExists("bill", "payments", "billItems", "paymentError"));

        verify(bills).findPayments(7L);
        verify(bills).findItems(7L);
    }
}
