package com.clinic.pharm.repository;

import com.clinic.pharm.domain.OutboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {
}
