package Product;

import java.sql.*;
import java.util.ArrayList;

public class ProductRepository {

    public static final String URL = "jdbc:sqlite:webbutiken.db";

    public ArrayList<Product> getAll() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                // Kolla vilka kolumner som faktiskt finns för att undvika SQL-fel
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stockQuantity = rs.getInt("stock_quantity");

                // Skapa en produkt med de kolumner vi vet finns
                Product product = new Product(
                        productId,
                        name,
                        price,
                        stockQuantity,
                        null,  // Sätt categoryName till null eftersom kolumnen inte finns
                        rs.getString("description") // Denna kolumn kan också vara null
                );
                products.add(product);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av produkter: " + e.getMessage());
        }
        return products;
    }

    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        productId,
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        null, // Sätt categoryName till null
                        rs.getString("description")
                );
            } else {
                return null; // Ingen produkt hittades
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av produkt: " + e.getMessage());
        }
    }

    public ArrayList<Product> getProductsByCategoryName(String categoryName) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();

        // Denna metod behöver troligen justeras baserat på din databasstruktur
        // Jag antar att du har en koppling mellan produkter och kategorier
        String sql = "SELECT p.* FROM products p " +
                "JOIN products_categories pc ON p.product_id = pc.product_id " +
                "JOIN categories c ON c.category_id = pc.category_id " +
                "WHERE c.name LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + categoryName + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        categoryName, // Använd den inkommande kategorin
                        rs.getString("description")
                );
                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av produkter efter kategori: " + e.getMessage());
        }
    }

    public void updateProductPrice(int productId, double newPrice) throws SQLException {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Priset kan inte vara negativt");
        }

        String sql = "UPDATE products SET price = ? WHERE product_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Uppdatering av pris misslyckades, ingen produkt hittades med ID: " + productId);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid uppdatering av produktpris: " + e.getMessage());
        }
    }

    public void updateProductStock(int productId, int newQuantity) throws SQLException {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Lagersaldo kan inte vara negativt");
        }

        String sql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Uppdatering av lagersaldo misslyckades, ingen produkt hittades med ID: " + productId);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid uppdatering av lagersaldo: " + e.getMessage());
        }
    }

    public void addProduct(String name, double price, int stockQuantity, String description) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Produktnamnet får inte vara tomt");
        }

        if (price < 0) {
            throw new IllegalArgumentException("Priset kan inte vara negativt");
        }

        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Lagersaldo kan inte vara negativt");
        }

        String sql = "INSERT INTO products (name, price, stock_quantity, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stockQuantity);
            pstmt.setString(4, description);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Fel vid tillägg av ny produkt: " + e.getMessage());
        }
    }

    public ArrayList<Product> searchProductsByName(String searchTerm) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        null, // Sätt categoryName till null
                        rs.getString("description")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid sökning av produkter: " + e.getMessage());
        }

        return products;
    }
}