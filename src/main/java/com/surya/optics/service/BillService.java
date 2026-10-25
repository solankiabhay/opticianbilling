package com.surya.optics.service;

import com.surya.optics.dto.BillFormDTO;
import com.surya.optics.entity.Bill;
import com.surya.optics.entity.Customer;
import com.surya.optics.entity.Payment;
import com.surya.optics.entity.Product;
import com.surya.optics.entity.BillItem;
import com.surya.optics.repository.BillRepository;
import com.surya.optics.repository.CustomerRepository;
import com.surya.optics.repository.PaymentRepository;
import com.surya.optics.repository.ProductRepository;
import com.surya.optics.repository.BillItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {
    private final BillRepository bills;
    private final CustomerRepository customers;
    private final PaymentRepository payments;
    private final ProductRepository products;
    private final BillItemRepository billItems;
    public BillService(BillRepository bills,CustomerRepository customers,PaymentRepository payments,ProductRepository products,BillItemRepository billItems){this.bills=bills;this.customers=customers;this.payments=payments;this.products=products;this.billItems=billItems;}

    @Transactional(readOnly=true) public List<Bill> findAll(){return bills.findAllByOrderByBillDateDesc();}
    @Transactional(readOnly=true) public List<Bill> findForCustomer(Long customerId){return bills.findByCustomerIdOrderByBillDateDesc(customerId);}
    @Transactional(readOnly=true) public Bill findById(Long id){return bills.findByIdWithCustomer(id).orElseThrow(()->new IllegalArgumentException("Bill not found: "+id));}
    @Transactional(readOnly=true) public List<Payment> findPayments(Long billId){return payments.findByBillIdOrderByPaidAtDesc(billId);}
    @Transactional(readOnly=true) public List<BillItem> findItems(Long billId){return billItems.findByBillIdOrderByIdAsc(billId);}

    @Transactional public Bill create(BillFormDTO d) {
        if(d.getCustomerId()==null)throw new IllegalArgumentException("Select a customer");
        Customer customer=customers.findById(d.getCustomerId()).orElseThrow(()->new IllegalArgumentException("Selected customer was not found"));
        BigDecimal subtotal=nonNegative(d.getSubtotal(),"Subtotal");
        BigDecimal contact=nonNegative(d.getContactLensCharges(),"Contact lens charges");
        BigDecimal sunglasses=nonNegative(d.getSunglassCharges(),"Sunglass charges");
        BigDecimal eyeTesting=nonNegative(d.getEyeTestingCharges(),"Eye testing charges");
        BigDecimal other=nonNegative(d.getOtherCharges(),"Other charges");
        java.util.Map<Long,Integer> requestedQuantities = new java.util.TreeMap<>();
        if(d.getProductIds()!=null && !d.getProductIds().isEmpty()) {
            for(int i=0;i<d.getProductIds().size();i++) {
                Long productId=d.getProductIds().get(i);
                if(productId==null) continue;
                Integer requested=i<d.getQuantities().size()?d.getQuantities().get(i):1;
                int quantity=requested==null?1:requested;
                if(quantity<1||quantity>10000)throw new IllegalArgumentException("Product quantity must be between 1 and 10000");
                int combined=requestedQuantities.getOrDefault(productId,0)+quantity;
                if(combined>10000)throw new IllegalArgumentException("Combined product quantity cannot exceed 10000");
                requestedQuantities.put(productId,combined);
            }
        } else if(d.getProductId()!=null) {
            int quantity=d.getQuantity()==null?1:d.getQuantity();
            if(quantity<1||quantity>10000)throw new IllegalArgumentException("Product quantity must be between 1 and 10000");
            requestedQuantities.put(d.getProductId(),quantity);
        }
        java.util.Map<Product,Integer> selectedProducts = new java.util.LinkedHashMap<>();
        BigDecimal productLineTotal=BigDecimal.ZERO;
        for(java.util.Map.Entry<Long,Integer> requested:requestedQuantities.entrySet()) {
            Product selected=products.findByIdForUpdate(requested.getKey()).orElseThrow(()->new IllegalArgumentException("Selected product was not found"));
            if(!selected.isActive())throw new IllegalArgumentException("Selected product is inactive");
            if(selected.getSellingPrice()==null||selected.getSellingPrice().compareTo(BigDecimal.ZERO)<0)throw new IllegalArgumentException("Selected product has an invalid selling price");
            if(selected.getStockQuantity()<requested.getValue())throw new IllegalArgumentException("Insufficient stock for "+selected.getName()+"; available: "+selected.getStockQuantity());
            selectedProducts.put(selected,requested.getValue());
            productLineTotal=productLineTotal.add(selected.getSellingPrice().multiply(BigDecimal.valueOf(requested.getValue())));
        }
        BigDecimal total=subtotal.add(productLineTotal).add(contact).add(sunglasses).add(eyeTesting).add(other);
        BigDecimal advance=nonNegative(d.getAdvance(),"Advance");
        if(advance.compareTo(total)>0)throw new IllegalArgumentException("Advance cannot exceed bill total");
        if(d.getDeliveryDays()!=null&&(d.getDeliveryDays()<0||d.getDeliveryDays()>365))throw new IllegalArgumentException("Delivery days must be between 0 and 365");
        Bill b=new Bill();b.setCustomer(customer);b.setBillDate(d.getBillDate()==null?LocalDate.now():d.getBillDate());
        b.setBillNumber("INV-"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase());b.setSubtotal(subtotal);b.setTotal(total);
        b.setAdvance(advance);b.setBalance(total.subtract(advance));b.setContactLensName(d.getContactLensName());
        b.setContactLensCharges(contact);b.setSunglassName(d.getSunglassName());b.setSunglassCharges(sunglasses);
        b.setEyeTestingCharges(eyeTesting);b.setOtherCharges(other);b.setGlassType(d.getGlassType());
        b.setDeliveryDays(d.getDeliveryDays());b.setDeliveryDate(d.getDeliveryDays()==null?null:b.getBillDate().plusDays(d.getDeliveryDays()));
        b.setPaymentMode(d.getPaymentMode());b.setNotes(d.getNotes());b.setStatus(b.getBalance().compareTo(BigDecimal.ZERO)==0?"PAID":"PENDING");
        Bill saved=bills.save(b);
        for(java.util.Map.Entry<Product,Integer> selected:selectedProducts.entrySet()) {
            Product product=selected.getKey();Integer quantity=selected.getValue();
            BillItem line=new BillItem();line.setBill(saved);line.setProduct(product);line.setProductName(product.getName());line.setProductSku(product.getSku());line.setQuantity(quantity);line.setUnitPrice(product.getSellingPrice());line.setLineTotal(product.getSellingPrice().multiply(BigDecimal.valueOf(quantity)));billItems.save(line);
            product.setStockQuantity(product.getStockQuantity()-quantity);products.save(product);
        }
        if(advance.compareTo(BigDecimal.ZERO)>0){Payment payment=new Payment();payment.setBill(saved);payment.setAmount(advance);payment.setPaymentMode(d.getPaymentMode());payment.setNote("Initial payment recorded when invoice was created");payments.save(payment);}
        return saved;
    }

    @Transactional public Bill recordPayment(Long billId,BigDecimal amount,String paymentMode) {
        Bill bill=bills.findByIdForUpdate(billId).orElseThrow(()->new IllegalArgumentException("Bill not found: "+billId));BigDecimal paymentAmount=nonNegative(amount,"Payment amount");
        if(paymentAmount.compareTo(BigDecimal.ZERO)<=0)throw new IllegalArgumentException("Payment amount must be greater than zero");
        BigDecimal currentAdvance=bill.getAdvance()==null?BigDecimal.ZERO:bill.getAdvance();
        BigDecimal currentBalance=bill.getBalance()==null?bill.getTotal().subtract(currentAdvance):bill.getBalance();
        if(paymentAmount.compareTo(currentBalance)>0)throw new IllegalArgumentException("Payment cannot exceed outstanding balance");
        bill.setAdvance(currentAdvance.add(paymentAmount));bill.setBalance(bill.getTotal().subtract(bill.getAdvance()));
        String mode=paymentMode==null||paymentMode.trim().isEmpty()?bill.getPaymentMode():paymentMode.trim();
        if(mode!=null&&!mode.isEmpty())bill.setPaymentMode(mode);
        bill.setStatus(bill.getBalance().compareTo(BigDecimal.ZERO)==0?"PAID":"PENDING");
        Bill saved=bills.save(bill);
        Payment receipt=new Payment();receipt.setBill(saved);receipt.setAmount(paymentAmount);receipt.setPaymentMode(mode);payments.save(receipt);
        return saved;
    }
    private BigDecimal nonNegative(BigDecimal value,String label){BigDecimal v=value==null?BigDecimal.ZERO:value;if(v.compareTo(BigDecimal.ZERO)<0)throw new IllegalArgumentException(label+" cannot be negative");return v;}
}
