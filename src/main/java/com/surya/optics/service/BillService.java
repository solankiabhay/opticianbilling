package com.surya.optics.service;

import com.surya.optics.dto.BillFormDTO;
import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Customer;
import com.surya.optics.repository.BillRepository;
import com.surya.optics.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {
    private final BillRepository bills;
    private final CustomerRepository customers;
    public BillService(BillRepository bills, CustomerRepository customers) { this.bills=bills; this.customers=customers; }

    @Transactional(readOnly=true) public List<Bill> findAll(){ return bills.findAllByOrderByBillDateDesc(); }
    @Transactional(readOnly=true) public List<Bill> findForCustomer(Long customerId){ return bills.findByCustomerIdOrderByBillDateDesc(customerId); }
    @Transactional(readOnly=true) public Bill findById(Long id){ return bills.findByIdWithCustomer(id).orElseThrow(() -> new IllegalArgumentException("Bill not found: "+id)); }

    @Transactional public Bill create(BillFormDTO d) {
        if(d.getCustomerId()==null) throw new IllegalArgumentException("Select a customer");
        Customer customer=customers.findById(d.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("Selected customer was not found"));
        BigDecimal subtotal=nonNegative(d.getSubtotal(),"Subtotal");
        BigDecimal extras=nonNegative(d.getContactLensCharges(),"Contact lens charges")
            .add(nonNegative(d.getSunglassCharges(),"Sunglass charges"))
            .add(nonNegative(d.getEyeTestingCharges(),"Eye testing charges"))
            .add(nonNegative(d.getOtherCharges(),"Other charges"));
        BigDecimal total=subtotal.add(extras);
        BigDecimal advance=nonNegative(d.getAdvance(),"Advance");
        if(advance.compareTo(total)>0) throw new IllegalArgumentException("Advance cannot exceed bill total");
        Bill b=new Bill(); b.setCustomer(customer); b.setBillDate(d.getBillDate()==null?LocalDate.now():d.getBillDate());
        b.setBillNumber("INV-"+System.currentTimeMillis()); b.setSubtotal(subtotal); b.setTotal(total);
        b.setAdvance(advance); b.setBalance(total.subtract(advance)); b.setContactLensName(d.getContactLensName());
        b.setContactLensCharges(nonNegative(d.getContactLensCharges(),"Contact lens charges"));
        b.setSunglassName(d.getSunglassName()); b.setSunglassCharges(nonNegative(d.getSunglassCharges(),"Sunglass charges"));
        b.setEyeTestingCharges(nonNegative(d.getEyeTestingCharges(),"Eye testing charges")); b.setOtherCharges(nonNegative(d.getOtherCharges(),"Other charges"));
        b.setGlassType(d.getGlassType()); b.setDeliveryDays(d.getDeliveryDays()); b.setDeliveryDate(d.getDeliveryDays()==null?null:b.getBillDate().plusDays(d.getDeliveryDays()));
        b.setPaymentMode(d.getPaymentMode()); b.setNotes(d.getNotes()); b.setStatus(b.getBalance().compareTo(BigDecimal.ZERO)==0?"PAID":"PENDING");
        return bills.save(b);
    }
    private BigDecimal nonNegative(BigDecimal value,String label){
        BigDecimal v=value==null?BigDecimal.ZERO:value;
        if(v.compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException(label+" cannot be negative");
        return v;
    }
}
