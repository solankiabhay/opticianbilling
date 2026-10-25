package com.surya.optics.controller;

import com.surya.optics.dto.BillFormDTO;
import com.surya.optics.service.BillService;
import com.surya.optics.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/bills")
public class BillController {
    private final BillService service; private final CustomerService customers;
    public BillController(BillService service,CustomerService customers){this.service=service;this.customers=customers;}
    @GetMapping public String list(Model model){model.addAttribute("bills",service.findAll());return "bills/list";}
    @GetMapping("/add") public String add(Model model){model.addAttribute("bill",new BillFormDTO());model.addAttribute("customers",customers.getAllCustomers());return "bills/add";}
    @PostMapping("/save") public String save(@ModelAttribute BillFormDTO dto,Model model){
        try{service.create(dto);return "redirect:/bills";}
        catch(IllegalArgumentException ex){model.addAttribute("bill",dto);model.addAttribute("customers",customers.getAllCustomers());model.addAttribute("error",ex.getMessage());return "bills/add";}
    }
    @GetMapping("/view/{id}") public String view(@PathVariable Long id,Model model){model.addAttribute("bill",service.findById(id));return "bills/view";}
    @PostMapping("/payment/{id}") public String payment(@PathVariable Long id,@RequestParam BigDecimal amount,@RequestParam(required=false) String paymentMode,Model model){
        try { service.recordPayment(id,amount,paymentMode); return "redirect:/bills/view/"+id; }
        catch(IllegalArgumentException ex) { model.addAttribute("bill",service.findById(id)); model.addAttribute("paymentError",ex.getMessage()); return "bills/view"; }
    }
}
