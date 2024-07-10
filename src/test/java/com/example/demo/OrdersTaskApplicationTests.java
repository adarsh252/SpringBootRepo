package com.example.demo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.Entities.OrderDetails;
import com.example.demo.Exception.ResourceNotFoundException;
import com.example.demo.Respository.OrderDetailsRepository;
import com.example.demo.Service.OrderDetailsService;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class OrdersTaskApplicationTests {

	@Mock
	private OrderDetailsRepository repository;

	@InjectMocks
	private OrderDetailsService service;

	private OrderDetails order1;
	private OrderDetails order2;
	private OrderDetails order3;
	
	@Value("${arrayOfOrderStatus}")
	private String[] arrayOfOrderStatus;

	@BeforeEach
	void setUp() throws Exception {
		//objects for testing
		order1 = OrderDetails.builder()
				.id(1L)
				.orderName("iphone")
				.orderStatus("placed")
				.build();
		
		order2 = OrderDetails.builder()
				.id(2L)
				.orderName("MacBook")
				.orderStatus("Delivered")
				.build();
		
		order3 = OrderDetails.builder()
				.id(3L)
				.orderName("Nike sneakers")
				.orderStatus("shipped")
				.build();
		
		
		//Used Reflection API for setting values for private array
		Class<? extends OrderDetailsService> c = service.getClass();
		Field[] fields = c.getDeclaredFields();
		for(Field field:fields) {
			if(field.getName().equals("arrayOfOrderStatus")) {
				field.setAccessible(true);
				field.set(service,arrayOfOrderStatus);
			}
		}
	}
    

	@DisplayName("Unit test for getDetails method")
	@Test
	void givenOrderDetailsList_whenGetAllOrderDetails_thenReturnOrderDetailsList() {
		// given
		// method stub
		given(repository.findAll()).willReturn(List.of(order1, order2));

		// when
		List<OrderDetails> orderDetails = service.getOrderDetails();

		// then
		Assertions.assertThat(orderDetails).isNotNull();
		Assertions.assertThat(orderDetails.size()).isEqualTo(2);

	}

	// Unit test for saveOrderDetails method
	@DisplayName("Unit test for saveOrderDetails method")
	@Test
	void givenOrderDetailsObject_whenSaveOrderDetailsObject_thenReturnOrderDetailsObject() {
		// given
		// method stub
		given(repository.save(order1)).willReturn(order1);

		// when
		OrderDetails savedOrderDetails = service.saveOrderDetails(order1);

		// then
		Assertions.assertThat(savedOrderDetails).isNotNull();

	}

	// Unit test for UpdateOrder method
	@DisplayName("Unit test for UpdateOrder method")
	@Test
	void givenOrderDetailsId_whenUpdateOrderObjectById_thenReturnUpdatedOrderDetailsObject()
			throws ResourceNotFoundException {
		// give
		// method stub
		Long orderId = order1.getId();
		given(repository.findById(orderId)).willReturn(Optional.of(order1));

		given(repository.save(order1)).willReturn(order1);

		order1.setOrderName("Airpods");
		order1.setOrderStatus("processed");

		// when

		OrderDetails updatedOrder = service.updateOrder(order1, orderId);

		// then

		Assertions.assertThat(updatedOrder.getOrderName()).isEqualTo("Airpods");
		Assertions.assertThat(updatedOrder.getOrderStatus()).isEqualTo("processed");
	}

	// Unit test for UpdateOrder method
	// Negative Scenario throws an exception
	@DisplayName("Unit test for UpdateOrder method when there is no existing Order")
	@Test
	void givenNonExistingOrderDetailsId_whenUpdateOrderObjectById_thenThrowsResourceNotFoundException()
			throws ResourceNotFoundException {
		// given
		Long orderId = 3L;
		// method stub
		given(repository.findById(orderId)).willReturn(Optional.empty());

		// when
		// then
		Assertions.assertThatExceptionOfType(ResourceNotFoundException.class)
				.isThrownBy(() -> service.updateOrder(order1, orderId))
				.withMessage("No resource found with id:" + orderId);
	}
	
	@DisplayName("Unit test for UpdateOrder method where we want to update order with Delivered order status")
	@Test
	void givenOrderDetailsIdWithOrderStatusDelivered_whenUpdateOrderObjectById_thenReturnsNull()
			throws ResourceNotFoundException {
		// given
		Long orderId = order2.getId();
		given(repository.findById(orderId)).willReturn(Optional.of(order2));
		// when
		OrderDetails updateOrder = service.updateOrder(order2, orderId);
		// then
		Assertions.assertThat(updateOrder).isNull();
	}
	
	@DisplayName("Unit test for UpdateOrder method where we want to update order with Inappropriate order status")
	@Test
	void givenOrderDetailsIdWithInappropriateOrderStatus_whenUpdateOrderObjectById_thenReturnsNull()
			throws ResourceNotFoundException {
		// given
		Long orderId = 3L;

		// when
		OrderDetails updateOrder = service.updateOrder(order3, orderId);
		// then
		Assertions.assertThat(updateOrder).isNull();
	}
	
	@DisplayName("Unit test for isOrderDelivered service method")
	@Test
	void givenExistingOrderDetailsId_whenIsOrderDeliveredById_thenReturnTrue() throws ResourceNotFoundException{
		//given

		//method stub
		
		given(repository.findById(order2.getId())).willReturn(Optional.of(order2));
		
		//when
		boolean orderDelivered = service.isOrderDelivered(order2.getId());
		
		//then
		Assertions.assertThat(orderDelivered).isEqualTo(true);
	
	}

	@DisplayName("Unit test for isOrderDelivered service method Negative scenario when orderStatus is not delivered")
	@Test
	void givenExistingOrderDetailsId_whenIsOrderDeliveredById_thenReturnFalse() throws ResourceNotFoundException{
		//given
		//method stub		
		given(repository.findById(order1.getId())).willReturn(Optional.of(order1));
		
		//when
		boolean orderDelivered = service.isOrderDelivered(order1.getId());
		
		//then
		Assertions.assertThat(orderDelivered).isEqualTo(false);
	
	}
	
	@DisplayName("Unit test for isOrderDelivered service method Negative scenario when there is no existing order")
	@Test
	void givenExistingOrderDetailsId_whenIsOrderDeliveredById_thenReturnResourceNotFoundException() throws ResourceNotFoundException{
		//given
		Long id = 4L;
		
		//method stub		
		given(repository.findById(id)).willReturn(Optional.empty());
		
		//when
		//then
		Assertions.assertThatExceptionOfType(ResourceNotFoundException.class)
		          .isThrownBy(()->service.isOrderDelivered(id))
		          .withMessage("No resource found with id:"+id);

	}

	
	@DisplayName("Unit test for checkOrderStatus service method")
    @Test
    void givenOrderObjectAndArrayOfOrderStatus_whenCheckOrderStatus_thenReturnfalse() {
        //given
        
        //when
        boolean result = service.checkOrderStatus(order1);

        // Assert
        Assertions.assertThat(result).isEqualTo(false);
    }
	
	
	@DisplayName("Unit test for checkOrderStatus service method Negative Scenario ")
    @Test
    void givenOrderObjectAndArrayOfOrderStatus_whenCheckOrderStatus_thenReturntrue() {
        //given
		
        //when
        boolean result = service.checkOrderStatus(order3);

        // Assert
        Assertions.assertThat(result).isEqualTo(true);
    }
	
	@DisplayName("Unit test for getErrorMessage service method")
	@Test
	void givenOrderDetailsObjectandId_whenGetErrorMessage_thenReturnsErrorMessage() throws ResourceNotFoundException{
		//given
		//when
		String errorMessage = service.getErrorMessage(order3, order3.getId());
		
		//then
		Assertions.assertThat(errorMessage).isEqualTo("invalid order status!");
	}
	
	@DisplayName("Unit test for getErrorMessage service method")
	@Test
	void givenOrderDetailsObjectandId_whenGetErrorMessage_thenReturnsErrorMessageWithId() throws ResourceNotFoundException{
		//given
      //method stub		
      		given(repository.findById(order2.getId())).willReturn(Optional.of(order2));
      		
		//when
		String errorMessage = service.getErrorMessage(order2, order2.getId());
		
		//then
		Assertions.assertThat(errorMessage).isEqualTo("order with id: " + order2.getId() + " already delivered!");
	}
	
	@DisplayName("Unit test for getErrorMessage service method returns ResourceNotFound exception")
	@Test
	void givenOrderDetailsObjectandId_whenGetErrorMessage_thenReturnsResouceNotFoundException() throws ResourceNotFoundException{
		//given
		Long id = 5L;
		
		//method stub		
		given(repository.findById(id)).willReturn(Optional.empty());
		
		//when
		//then
		Assertions.assertThatExceptionOfType(ResourceNotFoundException.class)
		          .isThrownBy(()->service.getErrorMessage(order2,id))
		          .withMessage("No resource found with id:"+id);
	}
}
