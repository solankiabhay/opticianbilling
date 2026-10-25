package com.surya.optics.service;

import com.surya.optics.dto.PrescriptionDTO;
import com.surya.optics.entity.Customer;
import com.surya.optics.entity.Prescription;
import com.surya.optics.repository.CustomerRepository;
import com.surya.optics.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptions;
    private final CustomerRepository customers;

    public PrescriptionService(PrescriptionRepository prescriptions, CustomerRepository customers) {
        this.prescriptions = prescriptions;
        this.customers = customers;
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDTO> findAll() {
        return prescriptions.findAllByOrderByPrescriptionDateDesc().stream().map(this::toDto).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> findForCustomer(Long customerId) {
        return prescriptions.findByCustomerIdOrderByPrescriptionDateDesc(customerId).stream().map(this::toDto).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public PrescriptionDTO findById(Long id) {
        return prescriptions.findById(id).map(this::toDto).orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));
    }
    @Transactional
    public PrescriptionDTO create(PrescriptionDTO dto) {
        Prescription p = new Prescription();
        apply(dto, p);
        return toDto(prescriptions.save(p));
    }
    @Transactional
    public PrescriptionDTO update(Long id, PrescriptionDTO dto) {
        Prescription p = prescriptions.findById(id).orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));
        apply(dto, p);
        return toDto(prescriptions.save(p));
    }
    @Transactional
    public void delete(Long id) {
        Prescription p = prescriptions.findById(id).orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));
        prescriptions.delete(p);
    }
    private void apply(PrescriptionDTO d, Prescription p) {
        if (d.getCustomerId() == null) throw new IllegalArgumentException("Select a customer");
        Customer c = customers.findById(d.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("Selected customer was not found"));
        p.setCustomer(c);
        p.setRightEyeSph(d.getRightSph()); p.setRightEyeCyl(d.getRightCyl()); p.setRightEyeAxis(d.getRightAxis()); p.setRightEyeVn(d.getRightVn());
        p.setLeftEyeSph(d.getLeftSph()); p.setLeftEyeCyl(d.getLeftCyl()); p.setLeftEyeAxis(d.getLeftAxis()); p.setLeftEyeVn(d.getLeftVn());
        p.setAddition(d.getAddition()); p.setGlassType(d.getGlassType()); p.setPrescriptionDate(d.getPrescriptionDate()); p.setExpiryDate(d.getExpiryDate()); p.setNotes(d.getNotes());
    }
    private PrescriptionDTO toDto(Prescription p) {
        PrescriptionDTO d = new PrescriptionDTO();
        d.setId(p.getId()); d.setCustomerId(p.getCustomer().getId()); d.setCustomerName(p.getCustomer().getName()); d.setCustomerPhone(p.getCustomer().getPhone());
        d.setBillId(p.getBill() == null ? null : p.getBill().getId()); d.setBillNumber(p.getBill() == null ? null : p.getBill().getBillNumber());
        d.setRightSph(p.getRightEyeSph()); d.setRightCyl(p.getRightEyeCyl()); d.setRightAxis(p.getRightEyeAxis()); d.setRightVn(p.getRightEyeVn());
        d.setLeftSph(p.getLeftEyeSph()); d.setLeftCyl(p.getLeftEyeCyl()); d.setLeftAxis(p.getLeftEyeAxis()); d.setLeftVn(p.getLeftEyeVn());
        d.setAddition(p.getAddition()); d.setGlassType(p.getGlassType()); d.setPrescriptionDate(p.getPrescriptionDate()); d.setExpiryDate(p.getExpiryDate()); d.setNotes(p.getNotes());
        return d;
    }
}
