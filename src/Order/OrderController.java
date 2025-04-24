package Order;

import Customer.Customer;
import Product.Product;
import Product.ProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderController {

    private Customer loggedInCustomer;
    private OrderService orderService;
    private ProductService productService;

    public OrderController(Customer customer) {
        this.loggedInCustomer = customer;
        this.orderService = new OrderService();
        this.productService = new ProductService();
    }

    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==== Order Menu ====");
            System.out.println("1. Se min orderhistorik");
            System.out.println("2. Skapa ny order");
            System.out.println("3. Återgå till huvudmenyn");
            System.out.print("Välj ett alternativ: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showOrderHistory();
                    break;
                case "2":
                    createNewOrder(scanner);
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Ogiltigt val. Försök igen.");
                    break;
            }
        }
    }

    private void showOrderHistory() {
        try {
            List<Order> orders = orderService.getOrderHistoryForCustomer(loggedInCustomer.getCustomerId());

            if (orders.isEmpty()) {
                System.out.println("Du har inga tidigare ordrar.");
                return;
            }

            System.out.println("\n==== Din orderhistorik ====");
            for (Order order : orders) {
                System.out.println("\nOrder #" + order.getOrderId());
                System.out.println("Datum: " + order.getOrderDate());
                System.out.println("Totalt: " + order.getTotalAmount() + " kr");

                System.out.println("Produkter:");
                for (OrderItem item : order.getOrderItems()) {
                    System.out.printf("- %s: %d st x %.2f kr = %.2f kr\n",
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getPriceAtPurchase(),
                            item.getSubtotal()
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }

    private void createNewOrder(Scanner scanner) {
        List<OrderItem> orderItems = new ArrayList<>();
        boolean addingProducts = true;

        try {
            while (addingProducts) {
                // Visa alla produkter
                List<Product> products = productService.getAllProducts();

                System.out.println("\n==== Tillgängliga produkter ====");
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    System.out.printf("%d. %s - %.2f kr (Lager: %d)\n",
                            i + 1,
                            product.getName(),
                            product.getPrice(),
                            product.getQuantity()
                    );
                }

                // Lägg till produkter i ordern
                System.out.print("\nVälj produktnummer (0 för att avsluta): ");
                int productIndex;
                try {
                    productIndex = Integer.parseInt(scanner.nextLine());
                    if (productIndex == 0) {
                        addingProducts = false;
                        continue;
                    }

                    if (productIndex < 1 || productIndex > products.size()) {
                        System.out.println("Ogiltigt produktnummer.");
                        continue;
                    }

                    Product selectedProduct = products.get(productIndex - 1);

                    System.out.print("Ange antal: ");
                    int quantity;
                    try {
                        quantity = Integer.parseInt(scanner.nextLine());

                        // Validera kvantitet
                        orderService.validateStock(selectedProduct.getProductId(), quantity);

                        OrderItem item = new OrderItem(selectedProduct, quantity);
                        orderItems.add(item);

                        System.out.println(selectedProduct.getName() + " lades till i ordern.");

                    } catch (NumberFormatException e) {
                        System.out.println("Ogiltigt antal. Ange ett heltal.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Ogiltigt val. Ange ett heltal.");
                }

                // Fråga om användaren vill lägga till fler produkter
                if (orderItems.size() > 0) {
                    System.out.print("\nVill du lägga till fler produkter? (j/n): ");
                    String answer = scanner.nextLine().toLowerCase();
                    if (!answer.equals("j")) {
                        addingProducts = false;
                    }
                }
            }

            // Slutför ordern om den har produkter
            if (!orderItems.isEmpty()) {
                Order order = orderService.createOrder(loggedInCustomer, orderItems);
                System.out.println("\nOrdern har skapats!");
                System.out.println("Order #" + order.getOrderId());
                System.out.println("Totalt: " + order.getTotalAmount() + " kr");
            } else {
                System.out.println("Ordern avbröts - inga produkter valda.");
            }

        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }
}