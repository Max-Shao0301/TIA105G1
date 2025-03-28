package com.orderpet.model;


import com.orders.model.OrdersVO;
import com.pet.model.PetVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert // 讓JAVA不會在SQL有預設值時 因為沒寫欄位而傳NULL過去
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private PetVO pet;
      
    
    @ManyToOne(fetch = FetchType.LAZY)
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
