package Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProductController {

    ProductService productService = new ProductService();

    public void runMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==== Produkt Meny ====");
            System.out.println("1. Visa alla produkter");
            System.out.println("2. Hämta en produkt efter ID");
            System.out.println("3. Sök produkter efter kategori");
            System.out.println("4. Sök produkter efter namn");
            System.out.println("5. Lägg till ny produkt");
            System.out.println("6. Uppdatera produktpris");
            System.out.println("7. Uppdatera lagersaldo");
            System.out.println("8. Återgå till huvudmenyn");
            System.out.print("Välj ett alternativ: ");

            String select = scanner.nextLine();

            try {
                switch (select) {
                    case "1":
                        showAllProducts();
                        break;
                    case "2":
                        getProductById(scanner);
                        break;
                    case "3":
                        searchProductsByCategory(scanner);
                        break;
                    case "4":
                        searchProductsByName(scanner);
                        break;
                    case "5":
                        addNewProduct(scanner);
                        break;
                    case "6":
                        updateProductPrice(scanner);
                        break;
                    case "7":
                        updateProductStock(scanner);
                        break;
                    case "8":
                        running = false;
                        break;
                    default:
                        System.out.println("Ogiltigt val. Försök igen.");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Ett databasfel inträffade: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Felaktig inmatning: " + e.getMessage());
            }
        }
    }

    private void showAllProducts() throws SQLException {
        ArrayList<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Inga produkter hittades.");
            return;
        }

        System.out.println("\n=== Alla produkter ===");
        for (Product p : products) {
            System.out.println("ID: " + p.getProductId());
            System.out.println("Namn: " + p.getName());
            System.out.println("Pris: " + p.getPrice() + " kr");
            System.out.println("Lager: " + p.getQuantity() + " st");
            System.out.println("---------------------");
        }
    }

    private void getProductById(Scanner scanner) throws SQLException {
        System.out.print("Ange produkt-ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Product product = productService.getProductById(id);

            if (product != null) {
                System.out.println("\n=== Produktdetaljer ===");
                System.out.println("ID: " + product.getProductId());
                System.out.println("Namn: " + product.getName());
                System.out.println("Pris: " + product.getPrice() + " kr");
                System.out.println("Lager: " + product.getQuantity() + " st");
            } else {
                System.out.println("Ingen produkt hittades med ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID. Ange ett heltal.");
        }
    }

    private void searchProductsByCategory(Scanner scanner) throws SQLException {
        System.out.print("Ange kategori att söka efter: ");
        String categoryName = scanner.nextLine();

        if (categoryName.trim().isEmpty()) {
            System.out.println("Kategorin får inte vara tom.");
            return;
        }

        try {
            productService.getProductsByCategoryName(categoryName);
        } catch (SQLException e) {
            System.out.println("Kunde inte söka efter kategori: " + e.getMessage());
        }
    }

    private void searchProductsByName(Scanner scanner) throws SQLException {
        System.out.print("Ange produktnamn att söka efter: ");
        String searchTerm = scanner.nextLine();

        if (searchTerm.trim().isEmpty()) {
            System.out.println("Söktermen får inte vara tom.");
            return;
        }

        ArrayList<Product> products = productService.searchProductsByName(searchTerm);

        if (products.isEmpty()) {
            System.out.println("Inga produkter matchade din sökning.");
            return;
        }

        System.out.println("\n=== Sökresultat för '" + searchTerm + "' ===");
        for (Product p : products) {
            System.out.println("ID: " + p.getProductId());
            System.out.println("Namn: " + p.getName());
            System.out.println("Pris: " + p.getPrice() + " kr");
            System.out.println("Lager: " + p.getQuantity() + " st");
            System.out.println("---------------------");
        }
    }

    private void addNewProduct(Scanner scanner) throws SQLException {
        System.out.println("\n=== Lägg till ny produkt ===");

        System.out.print("Namn: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Namnet får inte vara tomt.");
            return;
        }

        double price;
        try {
            System.out.print("Pris: ");
            price = Double.parseDouble(scanner.nextLine());
            if (price < 0) {
                System.out.println("Priset får inte vara negativt.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt pris. Ange ett nummer.");
            return;
        }

        int stockQuantity;
        try {
            System.out.print("Lagersaldo: ");
            stockQuantity = Integer.parseInt(scanner.nextLine());
            if (stockQuantity < 0) {
                System.out.println("Lagersaldot får inte vara negativt.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt lagersaldo. Ange ett heltal.");
            return;
        }

        System.out.print("Beskrivning: ");
        String description = scanner.nextLine();

        try {
            productService.addProduct(name, price, stockQuantity, description);
            System.out.println("Produkten har lagts till!");
        } catch (SQLException e) {
            System.out.println("Kunde inte lägga till produkt: " + e.getMessage());
        }
    }

    private void updateProductPrice(Scanner scanner) throws SQLException {
        System.out.println("\n=== Uppdatera produktpris ===");

        int productId;
        try {
            System.out.print("Ange produkt-ID: ");
            productId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID. Ange ett heltal.");
            return;
        }

        // Verifiera att produkten finns
        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Ingen produkt hittades med ID: " + productId);
            return;
        }

        System.out.println("Nuvarande pris för " + product.getName() + ": " + product.getPrice() + " kr");

        double newPrice;
        try {
            System.out.print("Ange nytt pris: ");
            newPrice = Double.parseDouble(scanner.nextLine());
            if (newPrice < 0) {
                System.out.println("Priset får inte vara negativt.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt pris. Ange ett nummer.");
            return;
        }

        try {
            productService.updateProductPrice(productId, newPrice);
            System.out.println("Priset har uppdaterats!");
        } catch (SQLException e) {
            System.out.println("Kunde inte uppdatera pris: " + e.getMessage());
        }
    }

    private void updateProductStock(Scanner scanner) throws SQLException {
        System.out.println("\n=== Uppdatera lagersaldo ===");

        int productId;
        try {
            System.out.print("Ange produkt-ID: ");
            productId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID. Ange ett heltal.");
            return;
        }

        // Verifiera att produkten finns
        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Ingen produkt hittades med ID: " + productId);
            return;
        }

        System.out.println("Nuvarande lagersaldo för " + product.getName() + ": " + product.getQuantity() + " st");

        int newQuantity;
        try {
            System.out.print("Ange nytt lagersaldo: ");
            newQuantity = Integer.parseInt(scanner.nextLine());
            if (newQuantity < 0) {
                System.out.println("Lagersaldot får inte vara negativt.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt lagersaldo. Ange ett heltal.");
            return;
        }

        try {
            productService.updateProductStock(productId, newQuantity);
            System.out.println("Lagersaldot har uppdaterats!");
        } catch (SQLException e) {
            System.out.println("Kunde inte uppdatera lagersaldo: " + e.getMessage());
        }
    }
}
