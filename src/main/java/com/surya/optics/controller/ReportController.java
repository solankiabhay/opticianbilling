package com.surya.optics.controller;

import com.surya.optics.entity.Bill;
import com.surya.optics.service.BillService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final BillService bills;
    public ReportController(BillService bills){this.bills=bills;}
    @GetMapping public String index(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate start,
                                   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate end,
                                   Model model){
        LocalDate to=end==null?LocalDate.now():end;
        LocalDate from=start==null?to.withDayOfMonth(1):start;
        if(from.isAfter(to)){model.addAttribute("error","Start date cannot be after end date.");from=to.withDayOfMonth(1);}
        final LocalDate rangeStart=from;final LocalDate rangeEnd=to;
        List<Bill> filtered=bills.findAll().stream().filter(b->b.getBillDate()!=null&&!b.getBillDate().isBefore(rangeStart)&&!b.getBillDate().isAfter(rangeEnd)).collect(Collectors.toList());
        model.addAttribute("start",rangeStart);model.addAttribute("end",rangeEnd);model.addAttribute("bills",filtered);
        model.addAttribute("invoiceCount",filtered.size());
        model.addAttribute("salesTotal",sum(filtered,Bill::getTotal));
        model.addAttribute("receivedTotal",sum(filtered,Bill::getAdvance));
        model.addAttribute("outstandingTotal",sum(filtered,Bill::getBalance));
        return "reports/index";
    }
    private BigDecimal sum(List<Bill> list,java.util.function.Function<Bill,BigDecimal> field){return list.stream().map(field).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);}
}
