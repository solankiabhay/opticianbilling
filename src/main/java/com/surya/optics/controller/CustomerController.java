package com.surya.optics.controller;

import com.surya.optics.dto.CustomerDTO;
import com.surya.optics.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

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
        model.addAttribute("customer", service.getCustomerById(id));
        model.addAttribute("bills", Collections.emptyList());
        model.addAttribute("prescriptions", Collections.emptyList());
        model.addAttribute("totalSpent", BigDecimal.ZERO);
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
    @GetMapping("/delete/{id}") public String delete(@PathVariable Long id) {
        service.deleteCustomer(id); return "redirect:/customers";
    }
}
