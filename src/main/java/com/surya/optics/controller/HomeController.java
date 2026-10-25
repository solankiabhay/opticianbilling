package com.surya.optics.controller;

import com.surya.optics.entity.Bill;
import com.surya.optics.service.BillService;
import com.surya.optics.service.CustomerService;
import com.surya.optics.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final BillService bills;
    private final CustomerService customers;
    private final ProductService products;
    public HomeController(BillService bills,CustomerService customers,ProductService products){this.bills=bills;this.customers=customers;this.products=products;}
    @GetMapping({"/","/home","/dashboard"})
    public String home(Model model) {
        List<Bill> allBills=bills.findAll();
        BigDecimal sales=allBills.stream().map(Bill::getTotal).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal outstanding=allBills.stream().map(Bill::getBalance).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal received=allBills.stream().map(Bill::getAdvance).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        model.addAttribute("customerCount",customers.getAllCustomers().size());
        model.addAttribute("invoiceCount",allBills.size());
        model.addAttribute("salesTotal",sales);
        model.addAttribute("receivedTotal",received);
        model.addAttribute("outstandingTotal",outstanding);
        model.addAttribute("lowStockCount",products.lowStockCount());
        model.addAttribute("recentBills",allBills.stream().limit(8).collect(Collectors.toList()));
        return "dashboard";
    }
}
