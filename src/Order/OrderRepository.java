package Order;

import Customer.Customer;
import Customer.CustomerRepository;
import Product.Product;
import Product.ProductRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

    public static final String URL = "jdbc:sqlite:webbutiken.db";
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    public OrderRepository() {
        this.customerRepository = new CustomerRepository();
        this.productRepository = new ProductRepository();
    }

    // Hjälpmetod för att hämta tabellstruktur
    private Map<String, Boolean> getTableColumns(Connection conn, String tableName) throws SQLException {
        Map<String, Boolean> columns = new HashMap<>();

        String checkSql = "PRAGMA table_info(" + tableName + ")";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            while (rs.next()) {
                String columnName = rs.getString("name");
                boolean notNull = rs.getInt("notnull") == 1;
                columns.put(columnName.toLowerCase(), notNull);
            }
        }

        return columns;
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

                // Hantera NULL vid timestamp-konvertering
                Timestamp orderDateTimestamp = rs.getTimestamp("order_date");
                LocalDateTime orderDate = orderDateTimestamp != null
                        ? orderDateTimestamp.toLocalDateTime()
                        : LocalDateTime.now();

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

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Hämta kolumnerna i orders_products
            Map<String, Boolean> columns = getTableColumns(conn, "orders_products");

            // Bestäm vilken kolumn som lagrar priset
            String priceColumn = null;
            if (columns.containsKey("price_at_purchase")) {
                priceColumn = "price_at_purchase";
            } else if (columns.containsKey("unit_price")) {
                priceColumn = "unit_price";
            }

            // Skapa SQL frågan baserat på tillgängliga kolumner
            StringBuilder sqlBuilder = new StringBuilder("SELECT order_id, product_id, quantity");
            if (priceColumn != null) {
                sqlBuilder.append(", ").append(priceColumn);
            }
            sqlBuilder.append(" FROM orders_products WHERE order_id = ?");

            try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");

                    // Hämta produktinformation
                    Product product = productRepository.getProductById(productId);
                    if (product != null) {
                        // Sätt produktens ID korrekt
                        product.setProductId(productId);

                        // Hämta priset antingen från tabell eller produkt
                        double priceAtPurchase;
                        if (priceColumn != null) {
                            priceAtPurchase = rs.getDouble(priceColumn);
                        } else {
                            priceAtPurchase = product.getPrice();
                        }

                        OrderItem orderItem = new OrderItem(0, product, quantity, priceAtPurchase);
                        orderItems.add(orderItem);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av orderdetaljer: " + e.getMessage());
        }

        return orderItems;
    }

    public int createOrder(Order order) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false); // Starta en transaktion

            String sql = "INSERT INTO orders (customer_id, order_date) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, order.getCustomer().getCustomerId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Kunde inte skapa order, ingen rad påverkades.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);

                // Lägg till alla orderItems
                for (OrderItem item : order.getOrderItems()) {
                    addProductToOrder(conn, order.getOrderId(), item);
                }

                conn.commit(); // Bekräfta transaktionen
                return orderId;
            } else {
                conn.rollback(); // Ångra transaktionen
                throw new SQLException("Kunde inte skapa order, inget ID erhölls.");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Ångra transaktionen vid fel
                } catch (SQLException ex) {
                    throw new SQLException("Kunde inte återställa transaktion: " + ex.getMessage());
                }
            }
            throw new SQLException("Fel vid skapande av order: " + e.getMessage());
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* Ignorera */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* Ignorera */ }
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) { /* Ignorera */ }
        }
    }

    private void addProductToOrder(Connection conn, int orderId, OrderItem item) throws SQLException {
        // Hämta tabellstruktur
        Map<String, Boolean> columns = getTableColumns(conn, "orders_products");

        // Skapa dynamisk SQL baserat på tabellstruktur
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO orders_products (order_id, product_id, quantity");

        boolean hasPriceAtPurchase = columns.containsKey("price_at_purchase");
        boolean hasUnitPrice = columns.containsKey("unit_price");

        if (hasPriceAtPurchase) {
            sqlBuilder.append(", price_at_purchase");
        }
        if (hasUnitPrice) {
            sqlBuilder.append(", unit_price");
        }

        sqlBuilder.append(") VALUES (?, ?, ?");

        if (hasPriceAtPurchase) {
            sqlBuilder.append(", ?");
        }
        if (hasUnitPrice) {
            sqlBuilder.append(", ?");
        }

        sqlBuilder.append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, item.getProduct().getProductId());
            pstmt.setInt(3, item.getQuantity());

            int paramIndex = 4;
            if (hasPriceAtPurchase) {
                pstmt.setDouble(paramIndex++, item.getPriceAtPurchase());
            }
            if (hasUnitPrice) {
                pstmt.setDouble(paramIndex++, item.getPriceAtPurchase());
            }

            pstmt.executeUpdate();

            // Uppdatera lagersaldo för produkten
            updateProductStock(conn, item.getProduct().getProductId(), item.getQuantity());
        } catch (SQLException e) {
            throw new SQLException("Fel när produkt lades till i order: " + e.getMessage());
        }
    }

    private void updateProductStock(Connection conn, int productId, int quantityToReduce) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityToReduce);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Kunde inte uppdatera lagersaldo för produkt med ID: " + productId);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid uppdatering av lagersaldo: " + e.getMessage());
        }
    }
}