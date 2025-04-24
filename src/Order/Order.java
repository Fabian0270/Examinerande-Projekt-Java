package Order;

import Customer.Customer;
import Product.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private Customer customer;
    private LocalDateTime orderDate;
    private List<OrderItem> orderItems;
    private double totalAmount;

    public Order() {
        this.orderItems = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
    }

    public Order(int orderId, Customer customer, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderDate = orderDate;
        this.orderItems = new ArrayList<>();
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        calculateTotalAmount();
    }

    public void calculateTotalAmount() {
        this.totalAmount = 0;
        for (OrderItem item : orderItems) {
            this.totalAmount += item.getSubtotal();
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculateTotalAmount();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customer=" + customer.getName() +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", items=" + orderItems.size() +
                '}';
    }
}