package com.retail.ProductApplication.Model;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "products")
public class Product {
    
	@Id
	@Column(name ="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long  id;
	
	@Column (name = "product_name")
	private String productName;
	
	@Column(name = "price")
	private int price;
	
	@Column(name = "posted_date")
	private LocalDateTime  postedDate;
	
	@JsonProperty
	@Column(name = "is_active")
	private boolean isActive;
	
	
	@Column(name = "approval_queue", nullable = true)
	private boolean approvalQueue;
	
	@JsonProperty
	 @Column(name = "is_approved",  nullable = true)
	 private boolean isApproved;
	 
	@JsonIgnore
	 @OneToOne(cascade = CascadeType.ALL)
	 @JoinColumn(name = "approval_id", unique = true
	 )
	 private ApprovalQueue approvalQueues;

	public ApprovalQueue getApprovalQueues() {
		return approvalQueues;
	}

	public void setApprovalQueues(ApprovalQueue approvalQueues) {
		this.approvalQueues = approvalQueues;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public LocalDateTime getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(LocalDateTime postedDate) {
		this.postedDate = postedDate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isApprovalQueue() {
		return approvalQueue;
	}

	public void setApprovalQueue(boolean approvalQueue) {
		this.approvalQueue = approvalQueue;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
	 
	 
}

	