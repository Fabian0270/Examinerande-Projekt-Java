package Product;

import java.sql.SQLException;
import java.util.ArrayList;

public class ProductService {

    ProductRepository productRepository = new ProductRepository();

    public ArrayList<Product> getAllProducts() throws SQLException {
        return productRepository.getAll();
    }

    public Product getProductById(int id) throws SQLException {
        return productRepository.getProductById(id);
    }

    public void getProductsByCategoryName(String categoryName) throws SQLException {
        ArrayList<Product> products = productRepository.getProductsByCategoryName(categoryName);
        for (Product p : products){
            System.out.println(p.toString());
        }

    }

    public void updateProductPrice(int productId, double newPrice) throws SQLException {
        try {
            if (newPrice < 0) {
                throw new IllegalArgumentException("Priset kan inte vara negativt");
            }
            productRepository.updateProductPrice(productId, newPrice);
            System.out.println("Priset har uppdaterats.");
        } catch (SQLException e) {
            throw new SQLException("Kunde inte uppdatera produktpris: " + e.getMessage());
        }
    }

    public void updateProductStock(int productId, int newQuantity) throws SQLException {
        try {
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Lagersaldo kan inte vara negativt");
            }
            productRepository.updateProductStock(productId, newQuantity);
            System.out.println("Lagersaldot har uppdaterats.");
        } catch (SQLException e) {
            throw new SQLException("Kunde inte uppdatera lagersaldo: " + e.getMessage());
        }
    }

    public void addProduct(String name, double price, int stockQuantity, String description) throws SQLException {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Produktnamnet får inte vara tomt");
            }

            if (price < 0) {
                throw new IllegalArgumentException("Priset kan inte vara negativt");
            }

            if (stockQuantity < 0) {
                throw new IllegalArgumentException("Lagersaldo kan inte vara negativt");
            }

            productRepository.addProduct(name, price, stockQuantity, description);
            System.out.println("Ny produkt har lagts till.");
        } catch (SQLException e) {
            throw new SQLException("Kunde inte lägga till ny produkt: " + e.getMessage());
        }
    }

    public ArrayList<Product> searchProductsByName(String searchTerm) throws SQLException {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                throw new IllegalArgumentException("Söktermen får inte vara tom");
            }
            return productRepository.searchProductsByName(searchTerm);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte söka efter produkter: " + e.getMessage());
        }
    }

}
