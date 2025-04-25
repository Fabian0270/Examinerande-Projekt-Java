import Customer.*;
import Order.OrderController;
import Product.ProductController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Välkommen till Webshop!");
            System.out.println("=======================");

            // Huvudmeny
            boolean running = true;
            while (running) {
                System.out.println("\n==== Huvudmeny ====");
                System.out.println("1. Kundhantering");
                System.out.println("2. Produkthantering");
                System.out.println("3. Orderhantering");
                System.out.println("4. Avsluta");
                System.out.print("Välj alternativ: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        CustomerController customerController = new CustomerController(null);
                        customerController.runMenu();
                        break;
                    case "2":
                        ProductController productController = new ProductController();
                        productController.runMenu();
                        break;
                    case "3":
                        OrderController orderController = new OrderController(null);
                        orderController.runMenu();
                        break;
                    case "4":
                        System.out.println("Tack för besöket! Välkommen åter.");
                        running = false;
                        break;
                    default:
                        System.out.println("Ogiltigt val. Försök igen.");
                }
            }

        } catch (Exception e) {
            System.out.println("Ett oväntat fel inträffade: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}