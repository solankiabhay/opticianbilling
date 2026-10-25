package com.surya.optics.controller;

import com.surya.optics.dto.CustomerDTO;
import com.surya.optics.dto.PrescriptionDTO;
import com.surya.optics.service.CustomerService;
import com.surya.optics.service.PrescriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionService service;
    private final CustomerService customers;
    public PrescriptionController(PrescriptionService service, CustomerService customers) {
        this.service = service; this.customers = customers;
    }
    @GetMapping public String list(Model model) {
        model.addAttribute("prescriptions", service.findAll());
        return "prescriptions/list";
    }
    @GetMapping("/add") public String add(Model model) {
        model.addAttribute("prescription", new PrescriptionDTO());
        model.addAttribute("customers", customers.getAllCustomers());
        return "prescriptions/add";
    }
    @PostMapping("/save") public String save(@ModelAttribute PrescriptionDTO dto, Model model) {
        try { service.create(dto); return "redirect:/prescriptions"; }
        catch (IllegalArgumentException ex) {
            model.addAttribute("prescription", dto); model.addAttribute("customers", customers.getAllCustomers()); model.addAttribute("error", ex.getMessage());
            return "prescriptions/add";
        }
    }
    @GetMapping("/edit/{id}") public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("prescription", service.findById(id)); model.addAttribute("customers", customers.getAllCustomers());
        return "prescriptions/edit";
    }
    @PostMapping("/update/{id}") public String update(@PathVariable Long id, @ModelAttribute PrescriptionDTO dto, Model model) {
        try { service.update(id, dto); return "redirect:/prescriptions"; }
        catch (IllegalArgumentException ex) {
            dto.setId(id); model.addAttribute("prescription", dto); model.addAttribute("customers", customers.getAllCustomers()); model.addAttribute("error", ex.getMessage());
            return "prescriptions/edit";
        }
    }
    @GetMapping("/view/{id}") public String view(@PathVariable Long id, Model model) {
        model.addAttribute("prescription", service.findById(id));
        return "prescriptions/view";
    }
    @PostMapping("/delete/{id}") public String delete(@PathVariable Long id) {
        service.delete(id); return "redirect:/prescriptions";
    }
}
