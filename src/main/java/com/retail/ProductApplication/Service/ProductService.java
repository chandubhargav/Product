package com.retail.ProductApplication.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.retail.ProductApplication.Model.ApprovalQueue;
import com.retail.ProductApplication.Model.Product;
import com.retail.ProductApplication.Repository.ApprovalQueueRepository;
import com.retail.ProductApplication.Repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	ProductRepository  productRepository;
	
	@Autowired
	ApprovalQueueRepository approvalQueueRepository;
	
	
	
	public List<Product> getAllProducts(){
		return productRepository.findByIsActiveTrueOrderByPostedDateDesc();
	}
	
	public List<Product>  searchByproductName(String productName){
		return productRepository.findByProductName(productName);
	}
	
	public List<Product>  searchByMinandMaxprice(int minPrice, int maxPrice){
		return productRepository.findByPriceBetween(minPrice, maxPrice);
	}
	
	public List<Product>  searchByPostedDate(Date minPostedDate , Date maxPostedDate){
		return productRepository.findByPostedDateBetween(minPostedDate, minPostedDate);
	}
	

	@Transactional
	public ResponseEntity<?> saveproduct(Product product){
		//price exceeds more than 5000 push to queue
		product.setApprovalQueue(product.getPrice()>5000?true:false);
			
		try {
		if(!product.isApprovalQueue()) {
		  productRepository.save(product);
		}  
		if(product.isApprovalQueue()) {
			//setting approval to false by default
			product.setApproved(false);
			
			ApprovalQueue a = new ApprovalQueue();
			a.setProduct(product);
			
		    a.setNotesForApproval("price is given more than 5000 and less than 10000 so needs approval to sell");
		    product.setApprovalQueues(a);
			productRepository.save(product);
		}
				
		}catch(Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		 if(product.isApprovalQueue()){
		 return new ResponseEntity<>("Product is on Approval State Since Price is given More", HttpStatus.OK);
		 }
		 return new ResponseEntity<>("Successfully product is created in Database", HttpStatus.CREATED);
		 }
	
	
	@Transactional
	public ResponseEntity<?> updateProduct(Product product, Long id){
		
		try {
			Optional<Product> p2 = productRepository.findById(id);
			Product p=p2.get();
			int val = p.getPrice()*(50/100);
		
			p.setApprovalQueue(product.getPrice()<val?true:false);
			p.setPrice(product.getPrice());
			p.setActive(product.isActive());
			p.setProductName(product.getProductName());
			p.setPostedDate(product.getPostedDate());
			
			if(!product.isApprovalQueue()) {
				productRepository.save(p);
			}  
			if(product.isApprovalQueue()) {
				//setting approval to false by default
				p.setApproved(false);
				
				ApprovalQueue a = new ApprovalQueue();
				a.setProduct(p);
				
			    a.setNotesForApproval("price is given more than 50 percent of previous price so needs approval to sell");
			    p.setApprovalQueues(a);
				productRepository.save(p);
			}
			
		}catch(Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		 if(product.isApprovalQueue()){
			 return new ResponseEntity<>("Product is on Appoval State Since Price is more than 50 percent", HttpStatus.OK);
			 }
		return new ResponseEntity<>("Successfully product is Updated", HttpStatus.OK);
		
	}
	
	
	@Transactional
	public ResponseEntity<?>  deleteProduct(Long productId){
		try {
			//Before Delete Check if you a trying to delete a Product already in Approval Queue
	
			Optional<Product> p1 = productRepository.findById(productId);
			Product p = p1.get();
			if(p.isApprovalQueue()) {
				return new ResponseEntity<>("Cannot Delete Product Which is in Approval Queue", HttpStatus.CONFLICT);
			}
			 productRepository.deleteById(productId);
			
			//Adding in to ApprovalQueue
			ApprovalQueue  a = new ApprovalQueue();
			//a.setProduct(p);
			//setting approval to false by default
			//p.setApproved(false);
			a.setNotesForApproval("Deleted a product SO Adding in to Approval Queue productId:"+productId);
			
			approvalQueueRepository.save(a);
		}catch (Exception e) {
			return new ResponseEntity<>("cant delete a Product got exception"+e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
		return new ResponseEntity<>("Deleted product Successfully with ProductId"+productId, HttpStatus.OK);
	}
	
	public ResponseEntity<?> getApprovalQueueProducts(){
		List<ApprovalQueue> approvalQueueList = new ArrayList<>();
		
		try {
			List<Product> productlist  =  productRepository.findAllByOrderByPostedDateAsc();
		//Getting Products only in ApprovalQueue to Return
			productlist = productlist.stream().filter(p->p.isApprovalQueue()==true && p.getApprovalQueues()!=null && p.getApprovalQueues().getId()!=null).collect(Collectors.toList());
			productlist.forEach(p->{
				ApprovalQueue ap = p.getApprovalQueues();
				Optional<ApprovalQueue> a = approvalQueueRepository.findById(p.getApprovalQueues().getId());
				ApprovalQueue a1 = a.get();
				approvalQueueList.add(a1);
			});
		}catch (Exception e) {
			return new ResponseEntity<>("Cant list approvalQueue got excpetion"+e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(approvalQueueList, HttpStatus.OK);
		
	}
	
	@Transactional
	public ResponseEntity<?> approveProducts(Long approvalId){
		ApprovalQueue approvalQueue = null;
		try {
			
			Optional<ApprovalQueue> approvalQueues = approvalQueueRepository.findById(approvalId);
			approvalQueue = approvalQueues.get();
			Product p = approvalQueue.getProduct();
			//approving to true in Product
			p.setApproved(true);
			p.setApprovalQueue(false);
			//breaking the relation bw approval and product so we can delete in approvalqueue
			p.setApprovalQueues(null);
			
			productRepository.save(p);
			
			//deleting from approvalQueue after approving
			
			approvalQueueRepository.deleteById(approvalId);
		}catch (Exception e) {
			return new ResponseEntity<>("cant approve the Product got exception"+e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<>("Successfully Approved the Product and Deleting the approvalId "+ approvalId+ "from Queue", HttpStatus.OK);
	}
	
	public ResponseEntity<?>  rejectProducts(Long approvalId){
		//Deleting from ApprovalQueue and not changing Product State
		ApprovalQueue approvalQueue = null;
		try{
			
			Optional<ApprovalQueue> approvalQueues = approvalQueueRepository.findById(approvalId);
			approvalQueue = approvalQueues.get();
			Product p = approvalQueue.getProduct();
			//approving to true in Product
			p.setApproved(false);
			p.setApprovalQueue(false);
			//breaking the relation bw approval and product so we can delete in approvalqueue
			p.setApprovalQueues(null);
			
			approvalQueueRepository.deleteById(approvalId);
		}catch(Exception e) {
			return new ResponseEntity<>("cant reject the approval got exception"+e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<>("Rejected the Approval and Deleted from Queue", HttpStatus.OK);
	}
	
	
 
}
