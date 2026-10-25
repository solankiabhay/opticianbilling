package com.surya.optics.repository;

import com.surya.optics.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findAllByOrderByPrescriptionDateDesc();
    List<Prescription> findByCustomerIdOrderByPrescriptionDateDesc(Long customerId);
}
