package com.orderpet.model;


import com.orders.model.OrdersVO;
import com.pet.model.PetVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_pet")
public class OrderPetVO implements java.io.Serializable {
	
    private static final long serialVersionUID = 1L;
    
    public OrderPetVO() {
    	
    }
    
    @Id
    @Column(name = "ordpet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ordPetId;
    
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetVO pet;
      
    
    @ManyToOne
    @JoinColumn(name = "order_Id")
    private OrdersVO orders;

    
	public Integer getOrdPetId() {
		return ordPetId;
	}


	public void setOrdPetId(Integer ordPetId) {
		this.ordPetId = ordPetId;
	}


	public PetVO getPet() {
		return pet;
	}


	public void setPet(PetVO pet) {
		this.pet = pet;
	}


	public OrdersVO getOrders() {
		return orders;
	}


	public void setOrders(OrdersVO orders) {
		this.orders = orders;
	}

	
 
  


    
}
