package com.surya.optics.controller;

import com.surya.optics.dto.BillFormDTO;
import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Product;
import com.surya.optics.service.ProductService;
import com.surya.optics.service.BillService;
import com.surya.optics.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/bills")
public class BillController {
    private final BillService service; private final CustomerService customers; private final ProductService products;
    public BillController(BillService service,CustomerService customers,ProductService products){this.service=service;this.customers=customers;this.products=products;}
    @GetMapping public String list(Model model){
        List<Bill> bills=service.findAll();
        BigDecimal billed=bills.stream().map(Bill::getTotal).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal outstanding=bills.stream().map(Bill::getBalance).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        model.addAttribute("bills",bills);model.addAttribute("billedTotal",billed);model.addAttribute("outstandingTotal",outstanding);
        return "bills/list";
    }
    @GetMapping("/add") public String add(Model model){model.addAttribute("bill",new BillFormDTO());model.addAttribute("customers",customers.getAllCustomers());model.addAttribute("products",activeProducts());return "bills/add";}
    @PostMapping("/save") public String save(@ModelAttribute BillFormDTO dto,Model model){
        try{service.create(dto);return "redirect:/bills";}
        catch(IllegalArgumentException ex){model.addAttribute("bill",dto);model.addAttribute("customers",customers.getAllCustomers());model.addAttribute("products",activeProducts());model.addAttribute("error",ex.getMessage());return "bills/add";}
    }
    @GetMapping("/view/{id}") public String view(@PathVariable Long id,Model model){model.addAttribute("bill",service.findById(id));model.addAttribute("payments",service.findPayments(id));model.addAttribute("billItems",service.findItems(id));return "bills/view";}
    @PostMapping("/payment/{id}") public String payment(@PathVariable Long id,@RequestParam BigDecimal amount,@RequestParam(required=false) String paymentMode,Model model){
        try { service.recordPayment(id,amount,paymentMode); return "redirect:/bills/view/"+id; }
        catch(IllegalArgumentException ex) { model.addAttribute("bill",service.findById(id)); model.addAttribute("payments",service.findPayments(id)); model.addAttribute("billItems",service.findItems(id)); model.addAttribute("paymentError",ex.getMessage()); return "bills/view"; }
    }
    private List<Product> activeProducts(){return products.findAll().stream().filter(Product::isActive).collect(java.util.stream.Collectors.toList());}
    @GetMapping(value="/export.csv", produces="text/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition","attachment; filename=surya-optics-invoices.csv");
        StringBuilder csv=new StringBuilder("\uFEFFInvoice,Customer,Phone,Date,Total,Received,Balance,Status,Payment Mode\r\n");
        for(Bill b:service.findAll()){
            csv.append(cell(b.getBillNumber())).append(',')
               .append(cell(b.getCustomer()==null?null:b.getCustomer().getName())).append(',')
               .append(cell(b.getCustomer()==null?null:b.getCustomer().getPhone())).append(',')
               .append(cell(b.getBillDate()==null?null:b.getBillDate().toString())).append(',')
               .append(number(b.getTotal())).append(',')
               .append(number(b.getAdvance())).append(',')
               .append(number(b.getBalance())).append(',')
               .append(cell(b.getStatus())).append(',')
               .append(cell(b.getPaymentMode())).append("\r\n");
        }
        response.getOutputStream().write(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
    private String number(BigDecimal n){return n==null?"0.00":n.toPlainString();}
    private String cell(String value){
        if(value==null)return "";
        String safe=value.replace("\r"," ").replace("\n"," ");
        int first = 0;
        while (first < safe.length() && (Character.isWhitespace(safe.charAt(first)) || Character.isISOControl(safe.charAt(first)))) first++;
        if (first < safe.length() && "=+-@".indexOf(safe.charAt(first)) >= 0) safe = "'" + safe;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }
}
