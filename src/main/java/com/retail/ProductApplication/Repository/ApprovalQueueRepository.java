package com.retail.ProductApplication.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retail.ProductApplication.Model.ApprovalQueue;

@Repository
public interface ApprovalQueueRepository extends JpaRepository<ApprovalQueue, Long> {

} 
