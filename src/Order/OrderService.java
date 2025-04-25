package Order;

import Customer.Customer;
import Product.Product;
import Product.ProductRepository;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
        this.productRepository = new ProductRepository();

        // Ta bort utskriften av databasschemat
        // orderRepository.printDatabaseSchema();
    }

    public List<Order> getOrderHistoryForCustomer(int customerId) throws SQLException {
        try {
            return orderRepository.getOrdersByCustomerId(customerId);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte hämta orderhistorik: " + e.getMessage());
        }
    }

    public Order createOrder(Customer customer, List<OrderItem> items) throws SQLException {
        if (customer == null) {
            throw new IllegalArgumentException("Kunden kan inte vara null");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Ordern måste innehålla minst en produkt");
        }

        // Kontrollera att kunden har ett giltigt ID
        if (customer.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Kunden har ett ogiltigt ID");
        }

        // Validera lagersaldo för varje orderItem
        for (OrderItem item : items) {
            validateStock(item.getProduct().getProductId(), item.getQuantity());
        }

        // Skapa order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderItems(items);

        try {
            // Skapa order i databasen utan extra utskrifter
            int orderId = orderRepository.createOrder(order);
            order.setOrderId(orderId);
            return order;
        } catch (SQLException e) {
            throw new SQLException("Kunde inte skapa order: " + e.getMessage());
        }
    }

    public void validateStock(int productId, int requestedQuantity) throws SQLException {
        try {
            Product product = productRepository.getProductById(productId);

            if (product == null) {
                throw new IllegalArgumentException("Produkten finns inte i sortimentet.");
            }

            if (requestedQuantity <= 0) {
                throw new IllegalArgumentException("Kvantiteten måste vara större än 0.");
            }

            if (requestedQuantity > product.getQuantity()) {
                throw new IllegalArgumentException(
                        "Otillräckligt lager för " + product.getName() + ". Tillgängligt: " +
                                product.getQuantity() + ", begärt: " + requestedQuantity
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Kunde inte validera lagersaldo: " + e.getMessage());
        }
    }

    public double calculateOrderTotal(Order order) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return 0;
        }

        double total = 0;
        for (OrderItem item : order.getOrderItems()) {
            total += item.getSubtotal();
        }
        return total;
    }
}