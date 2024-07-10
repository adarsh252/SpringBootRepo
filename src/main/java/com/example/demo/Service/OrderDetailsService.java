package com.example.demo.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.demo.Entities.OrderDetails;
import com.example.demo.Exception.ResourceNotFoundException;
import com.example.demo.Respository.OrderDetailsRepository;



@Service
public class OrderDetailsService {

	@Autowired
	private OrderDetailsRepository ordersRepository;
	
	@Value("${arrayOfOrderStatus}")
	private String[] arrayOfOrderStatus;
	

	public OrderDetailsService(String[] arrayOfOrderStatus) {
		super();
		this.arrayOfOrderStatus = arrayOfOrderStatus;
	}

	//getting all the orders from the database
	public List<OrderDetails> getOrderDetails() {
		return ordersRepository.findAll();
	}

//saving orders to the database
	public OrderDetails saveOrderDetails(OrderDetails order) {
		OrderDetails savedOrder = ordersRepository.save(order);
		return savedOrder;
	}

//updating the existing order into database
	public OrderDetails updateOrder(OrderDetails order, Long id) throws ResourceNotFoundException {
		OrderDetails updatedOrder = null;
		if (!checkOrderStatus(order) && !isOrderDelivered(id)) {
			OrderDetails existingOrder = ordersRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No resource found with id:"+id));
			existingOrder.setOrderName(order.getOrderName());
			existingOrder.setOrderStatus(order.getOrderStatus());
			updatedOrder = ordersRepository.save(existingOrder);		
		}
		return updatedOrder;
	}

//checking the status of order delivered or not
	public boolean isOrderDelivered(Long id)throws ResourceNotFoundException{
		OrderDetails existingOrder = ordersRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No resource found with id:"+id));
		if (existingOrder.getOrderStatus().equalsIgnoreCase("Delivered")) {
			return true;
		}
		return false;	
	}

//checking whether order status is appropriate or not
	public boolean checkOrderStatus(OrderDetails order) {
		int flag = 0;
		for (String items : arrayOfOrderStatus) {
			if (order.getOrderStatus().equalsIgnoreCase(items)) {
				flag = 1;
				break;
			}
		}
		if (flag == 1) {
			return false;
		} else {

			return true;
		}
	}


	//checking order details eligible for update or not
	public String getErrorMessage(OrderDetails order, Long id)throws ResourceNotFoundException {

		// checking order status is appropriate or not order status should be
		// PLACED,PROCESSED,DELIVERED
		if (checkOrderStatus(order)) {

			return "invalid order status!";
		}
		// checking order status already delivered or not
		else if (isOrderDelivered(id)) {

			return "order with id: " + id + " already delivered!";
		}

		return "";

	} 

}
