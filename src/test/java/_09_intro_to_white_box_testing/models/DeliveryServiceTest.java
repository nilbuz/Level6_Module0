package _09_intro_to_white_box_testing.models;

import _08_mocking.models.DeliveryDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/*
In the previous unit testing recipes, we examined that the results of calling a method was the result that we were expecting.
This is called black-box testing.  We aren't peering into the method to test how it actually works, we simply invoke the
method and check that it gives us back was we expect.


There are some scenarios when black-box testing will not suffice:
-When the method returns void.
-When we want to verify that a specific behavior occurred within the method.

The first reason above should be clear: we simply cannot verify that a method returns what we are expecting if the method
returns nothing at all.  The second reason is especially important for methods that have a lot of branching logic.

In these scenarios, we want to engage in white-box testing.  White-box testing examines what the method does while it is
being, rather than simply taking a look at what it returns.  There is additional unit testing syntax that allows us to
verify that specific things occurred when the method was called.

 */
class DeliveryServiceTest {

    DeliveryService deliveryService;

    @Mock
    DeliveryDriver deliveryDriver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        List<DeliveryDriver> availableDeliveryDrivers = Collections.singletonList(deliveryDriver);
        deliveryService = new DeliveryService(availableDeliveryDrivers);
    }

    @Test
    void itShouldRefuelAllCars() {
        //given
        int octaneGrade = 85;

        //when
        deliveryService.refuelAllCars(octaneGrade);

        //then
        verify(deliveryDriver, times(1)).refuel(octaneGrade);
    }

    @Test
    void itShouldDeliver() throws Exception {
        //given
        Order order = new Order("CUSTOMER_NAME",
                "CUSTOMER_PHONE_NUMBER",
                1,
                5.00,
                "CREDIT_CARD_NUMBER",
                true);
        List<Order> orders = Collections.singletonList(order);
        deliveryService.setOrders(orders);
        when(deliveryDriver.completeDelivery(order)).thenReturn(true);

        //when
        deliveryService.deliver();

        //then
        verify(deliveryDriver, times(1)).completeDelivery(order);
    }

    @Test
    void givenDeliveryNotCompleted_whenDeliver_thenContactCustomer() throws Exception {
        //given
        String customerPhoneNumber = "CUSTOMER_PHONE_NUMBER";
        Order order = new Order("CUSTOMER_NAME",
                customerPhoneNumber,
                1,
                5.00,
                "CREDIT_CARD_NUMBER",
                true);
        List<Order> orders = Collections.singletonList(order);
        deliveryService.setOrders(orders);
        when(deliveryDriver.completeDelivery(order)).thenReturn(false);

        //when
        deliveryService.deliver();

        //then
        verify(deliveryDriver, times(1)).completeDelivery(order);
        verify(deliveryDriver, times(1)).contactCustomer(customerPhoneNumber);
    }

    @Test
    void givenNoDrivers_whenDeliver_thenThrowException(){
        //given
        List<DeliveryDriver> emptyDrivers = new ArrayList<>();
        deliveryService.setAvailableDeliveryDrivers(emptyDrivers);

        //when
        //then
        Throwable exceptionThrown = assertThrows(Exception.class, () -> deliveryService.deliver());
        assertEquals(exceptionThrown.getMessage(), "Sorry we currently do not have the resources available to complete more deliveries");
    }

    @Test
    void givenNoOrders_whenDeliver_thenThrowException() {
        //given
        List<Order> emptyOrders = new ArrayList<>();
        deliveryService.setOrders(emptyOrders);

        //when
        //then
        Throwable exceptionThrown = assertThrows(Exception.class, () -> deliveryService.deliver());
        assertEquals(exceptionThrown.getMessage(), "There are currently no orders to deliver");
    }


    @Test
    void itShouldScheduleDelivery() throws Exception {
        //given
        Order order = new Order("CUSTOMER_NAME",
                "CUSTOMER_PHONE_NUMBER",
                1,
                5.00,
                "CREDIT_CARD_NUMBER",
                true);

        //when
        //then
        assertDoesNotThrow(()->deliveryService.scheduleDelivery(order));
    }

    @Test
    void givenNotAcceptingOrders_whenScheduleDelivery_thenThrows() throws Exception {
        //given
        Order order = new Order("CUSTOMER_NAME",
                "CUSTOMER_PHONE_NUMBER",
                1,
                5.00,
                "CREDIT_CARD_NUMBER",
                true);
        deliveryService.setAcceptingOrders(false);

        //when
        //then
        Throwable exceptionThrown = assertThrows(Exception.class, () -> deliveryService.scheduleDelivery(order));
        assertEquals(exceptionThrown.getMessage(), "Sorry we are not currently accepting orders");
    }

}