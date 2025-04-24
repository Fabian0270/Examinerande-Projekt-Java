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