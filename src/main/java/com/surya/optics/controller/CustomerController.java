package com.surya.optics.controller;

import com.surya.optics.dto.CustomerDTO;
import com.surya.optics.service.CustomerService;
import com.surya.optics.service.BillService;
import com.surya.optics.service.PrescriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;


@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService service;
    private final BillService billService;
    private final PrescriptionService prescriptionService;
    public CustomerController(CustomerService service, BillService billService, PrescriptionService prescriptionService) { this.service = service; this.billService = billService; this.prescriptionService = prescriptionService; }

    @GetMapping public String list(Model model) {
        model.addAttribute("customers", service.getAllCustomers());
        return "customers/list";
    }
    @GetMapping("/add") public String add(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "customers/add";
    }
    @PostMapping("/save") public String save(@Valid @ModelAttribute CustomerDTO dto, Model model) {
        try { service.createCustomer(dto); return "redirect:/customers"; }
        catch (IllegalArgumentException ex) { model.addAttribute("customer", dto); model.addAttribute("error", ex.getMessage()); return "customers/add"; }
    }
    @GetMapping("/edit/{id}") public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("customer", service.getCustomerById(id));
        return "customers/edit";
    }
    @PostMapping("/update/{id}") public String update(@PathVariable Long id, @Valid @ModelAttribute CustomerDTO dto, Model model) {
        try { service.updateCustomer(id, dto); return "redirect:/customers"; }
        catch (IllegalArgumentException ex) { dto.setId(id); model.addAttribute("customer", dto); model.addAttribute("error", ex.getMessage()); return "customers/edit"; }
    }
    @GetMapping("/view/{id}") public String view(@PathVariable Long id, Model model) {
        CustomerDTO customer = service.getCustomerById(id);
        java.util.List<com.surya.optics.entity.Bill> bills = billService.findForCustomer(id);
        model.addAttribute("customer", customer);
        model.addAttribute("bills", bills);
        model.addAttribute("prescriptions", prescriptionService.findForCustomer(id));
        model.addAttribute("totalSpent", bills.stream().map(com.surya.optics.entity.Bill::getTotal).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "customers/view";
    }
    @GetMapping("/history/{id}") public String history(@PathVariable Long id, Model model) {
        return "redirect:/customers/view/" + id;
    }
    @GetMapping("/search") public String search(@RequestParam(required=false) String keyword, Model model) {
        model.addAttribute("customers", service.searchCustomers(keyword));
        model.addAttribute("keyword", keyword);
        return "customers/list";
    }
    @GetMapping("/api/customers/search") @ResponseBody
    public CustomerDTO searchByPhone(@RequestParam String phone) { return service.getCustomerByPhone(phone); }
    @PostMapping("/delete/{id}") public String delete(@PathVariable Long id) {
        service.deleteCustomer(id); return "redirect:/customers";
    }
}
