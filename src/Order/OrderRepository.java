package Order;

import Customer.Customer;
import Customer.CustomerRepository;
import Product.Product;
import Product.ProductRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public static final String URL = "jdbc:sqlite:webbutiken.db";
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    public OrderRepository() {
        this.customerRepository = new CustomerRepository();
        this.productRepository = new ProductRepository();
    }

    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders WHERE customer_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Customer customer = customerRepository.getCustomerById(customerId);
                LocalDateTime orderDate = rs.getTimestamp("order_date").toLocalDateTime();

                Order order = new Order(orderId, customer, orderDate);

                // Hämta orderItems för denna order
                List<OrderItem> orderItems = getOrderItemsByOrderId(orderId);
                order.setOrderItems(orderItems);

                orders.add(order);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av kundorder: " + e.getMessage());
        }

        return orders;
    }

    private List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> orderItems = new ArrayList<>();

        String sql = "SELECT * FROM orders_products WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double priceAtPurchase = rs.getDouble("price_at_purchase");

                Product product = productRepository.getProductById(productId);
                OrderItem orderItem = new OrderItem(0, product, quantity, priceAtPurchase);

                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av orderdetaljer: " + e.getMessage());
        }

        return orderItems;
    }

    public int createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, order_date) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getCustomer().getCustomerId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Kunde inte skapa order, ingen rad påverkades.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);

                    // Lägg till alla orderItems
                    for (OrderItem item : order.getOrderItems()) {
                        addProductToOrder(order.getOrderId(), item);
                    }

                    return orderId;
                } else {
                    throw new SQLException("Kunde inte skapa order, inget ID erhölls.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid skapande av order: " + e.getMessage());
        }
    }

    private void addProductToOrder(int orderId, OrderItem item) throws SQLException {
        String sql = "INSERT INTO orders_products (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, item.getProduct().getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPriceAtPurchase());

            pstmt.executeUpdate();

            // Uppdatera lagersaldo för produkten
            updateProductStock(item.getProduct().getProductId(), item.getQuantity());

        } catch (SQLException e) {
            throw new SQLException("Fel när produkt lades till i order: " + e.getMessage());
        }
    }

    private void updateProductStock(int productId, int quantityToReduce) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityToReduce);
            pstmt.setInt(2, productId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Fel vid uppdatering av lagersaldo: " + e.getMessage());
        }
    }
}
