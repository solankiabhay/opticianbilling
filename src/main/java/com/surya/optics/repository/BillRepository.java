package com.surya.optics.repository;

import com.surya.optics.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT b FROM Bill b JOIN FETCH b.customer ORDER BY b.billDate DESC")
    List<Bill> findAllByOrderByBillDateDesc();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bill b JOIN FETCH b.customer WHERE b.id = :id")
    Optional<Bill> findByIdForUpdate(@Param("id") Long id);
    @Query("SELECT b FROM Bill b JOIN FETCH b.customer WHERE b.id = :id")
    Optional<Bill> findByIdWithCustomer(@Param("id") Long id);
    @Query("SELECT b FROM Bill b JOIN FETCH b.customer WHERE b.customer.id = :customerId ORDER BY b.billDate DESC")
    List<Bill> findByCustomerIdOrderByBillDateDesc(@Param("customerId") Long customerId);
    long countByCustomerId(Long customerId);
}
