package com.surya.optics.controller;

import com.surya.optics.entity.Product;
import com.surya.optics.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service){this.service=service;}
    @GetMapping public String list(@RequestParam(required=false) String q,Model model){model.addAttribute("products",service.search(q));model.addAttribute("q",q);model.addAttribute("lowStockCount",service.lowStockCount());return "products/list";}
    @GetMapping("/add") public String add(Model model){model.addAttribute("product",new Product());return "products/form";}
    @GetMapping("/edit/{id}") public String edit(@PathVariable Long id,Model model){model.addAttribute("product",service.find(id));return "products/form";}
    @PostMapping("/save") public String save(@ModelAttribute Product product,Model model){
        try{service.save(product);return "redirect:/products";}
        catch(IllegalArgumentException ex){model.addAttribute("product",product);model.addAttribute("error",ex.getMessage());return "products/form";}
    }
    @PostMapping("/stock/{id}") public String stock(@PathVariable Long id,@RequestParam Integer delta,Model model){
        try{service.adjustStock(id,delta);return "redirect:/products";}
        catch(IllegalArgumentException ex){model.addAttribute("products",service.findAll());model.addAttribute("q",null);model.addAttribute("lowStockCount",service.lowStockCount());model.addAttribute("error",ex.getMessage());return "products/list";}
    }
    @PostMapping("/deactivate/{id}") public String deactivate(@PathVariable Long id){service.deactivate(id);return "redirect:/products";}
}
