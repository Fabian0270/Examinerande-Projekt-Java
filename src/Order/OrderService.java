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
    }

    public List<Order> getOrderHistoryForCustomer(int customerId) throws SQLException {
        try {
            return orderRepository.getOrdersByCustomerId(customerId);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte hämta orderhistorik: " + e.getMessage());
        }
    }

    public Order createOrder(Customer customer, List<OrderItem> items) throws SQLException {
        // Validera lagersaldo för varje orderItem
        for (OrderItem item : items) {
            validateStock(item.getProduct().getProductId(), item.getQuantity());
        }

        // Skapa order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderItems(items);

        try {
            int orderId = orderRepository.createOrder(order);
            return orderRepository.getOrdersByCustomerId(customer.getCustomerId())
                    .stream()
                    .filter(o -> o.getOrderId() == orderId)
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte skapa order: " + e.getMessage());
        }
    }

    public void validateStock(int productId, int requestedQuantity) throws SQLException {
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
    }

    public double calculateOrderTotal(Order order) {
        double total = 0;
        for (OrderItem item : order.getOrderItems()) {
            total += item.getSubtotal();
        }
        return total;
    }
}