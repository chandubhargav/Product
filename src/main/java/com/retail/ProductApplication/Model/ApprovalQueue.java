package com.retail.ProductApplication.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "approval_queue")
public class ApprovalQueue {
      
	 @Id
	 @Column(name ="id")
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 
	 @OneToOne( mappedBy = "approvalQueues")
	 private Product  product;
	 
	 @Column(name = "notes_for_approval")
	 private String notesForApproval;
	 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getNotesForApproval() {
		return notesForApproval;
	}

	public void setNotesForApproval(String notesForApproval) {
		this.notesForApproval = notesForApproval;
	}
	 
	 
	 
	 
	 
	 
}
