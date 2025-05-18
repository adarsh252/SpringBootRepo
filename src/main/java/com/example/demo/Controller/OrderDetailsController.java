package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Entities.OrderDetails;
import com.example.demo.Exception.ResourceNotFoundException;
import com.example.demo.Service.OrderDetailsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class OrderDetailsController {

	@Autowired
	private OrderDetailsService orderService;

	// URL: http://localhost:8080/api/
	// getting all the order details from the database
	@GetMapping("/")
	public ResponseEntity<List<OrderDetails>> getOrderDetails() {
		return new ResponseEntity<>(orderService.getOrderDetails(), HttpStatus.OK);
	}

	// URL: http://localhost:8080/api/orders
	// inserting a new order into the database
	@PostMapping("orders")
	public ResponseEntity<String> saveDetails(@Valid @RequestBody OrderDetails order) {
		// checking order status is appropriate or not order status should be
		// PLACED,PROCESSED,DELIVERED
		if (orderService.checkOrderStatus(order)) {
			return new ResponseEntity<>("Invalid order status!", HttpStatus.BAD_REQUEST);
		}
		// saving the order details into the database and getting the order id in
		// response
		OrderDetails saveOrderDetails = orderService.saveOrderDetails(order);
		//Long id = saveOrderDetails.getId();
		return new ResponseEntity<>("order with id: " + id + " placed!", HttpStatus.CREATED);
	}

	// URL: http://localhost:8080/api/orders/{id}
	// updating existing orders in database
		@PutMapping("orders/{id}")
		public ResponseEntity<String> updateOrderDetails(@Valid @RequestBody OrderDetails order,
				                                                @PathVariable("id") Long id)throws ResourceNotFoundException{
			OrderDetails updatedOrder = orderService.updateOrder(order, id);
			if (updatedOrder!=null) {
				// updating order in the database
				return new ResponseEntity<>("Order with id: " + updatedOrder.getId() + " updated!", HttpStatus.OK);
			}
	
			// getting the exact error from getErrorMessage
			return new ResponseEntity<>(orderService.getErrorMessage(order, id), HttpStatus.BAD_REQUEST);
	
		}

}
