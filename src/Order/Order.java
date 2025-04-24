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

package Order;

import Product.Product;

public class OrderItem {
    private int orderItemId;
    private Product product;
    private int quantity;
    private double priceAtPurchase;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = product.getPrice();
    }

    public OrderItem(int orderItemId, Product product, int quantity, double priceAtPurchase) {
        this.orderItemId = orderItemId;
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public double getSubtotal() {
        return quantity * priceAtPurchase;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "product=" + product.getName() +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}