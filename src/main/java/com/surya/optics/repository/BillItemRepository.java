package com.surya.optics.repository;

import com.surya.optics.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillItemRepository extends JpaRepository<BillItem,Long> {
    List<BillItem> findByBillIdOrderByIdAsc(Long billId);
}
