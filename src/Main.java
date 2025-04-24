import Customer.*;
import Order.OrderController;
import Product.ProductController;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CustomerRepository customerRepository = new CustomerRepository();

        try {
            System.out.println("Välkommen till Webshop!");
            System.out.println("=======================");

            System.out.println("1. Logga in");
            System.out.println("2. Registrera ny kund");
            System.out.print("Välj alternativ: ");

            String choice = scanner.nextLine();

            Customer loggedInCustomer = null;

            if (choice.equals("1")) {
                // Inloggning
                System.out.print("Ange email: ");
                String email = scanner.nextLine();
                System.out.print("Ange lösenord: ");
                String password = scanner.nextLine();

                try {
                    loggedInCustomer = customerRepository.login(email, password);
                    System.out.println("Välkommen " + loggedInCustomer.getName() + "!");
                } catch (SQLException e) {
                    System.out.println("Inloggningen misslyckades: " + e.getMessage());
                    System.exit(0);
                }
            } else if (choice.equals("2")) {
                // Registrering
                System.out.println("\n=== Registrera ny kund ===");
                System.out.print("Namn: ");
                String name = scanner.nextLine();

                System.out.print("Email: ");
                String email = scanner.nextLine();

                System.out.print("Telefon: ");
                String phone = scanner.nextLine();

                System.out.print("Adress: ");
                String address = scanner.nextLine();

                System.out.print("Lösenord: ");
                String password = scanner.nextLine();

                try {
                    CustomerService customerService = new CustomerService(null);
                    customerService.addCustomer(name, phone, email, address, password);
                    System.out.println("Registrering lyckades! Logga in nu.");

                    // Automatisk inloggning efter registrering
                    loggedInCustomer = customerRepository.login(email, password);
                    System.out.println("Välkommen " + loggedInCustomer.getName() + "!");
                } catch (SQLException e) {
                    System.out.println("Registreringen misslyckades: " + e.getMessage());
                    System.exit(0);
                }
            } else {
                System.out.println("Ogiltigt val. Avslutar programmet.");
                System.exit(0);
            }

            // Huvudmeny när inloggad
            boolean running = true;
            while (running && loggedInCustomer != null) {
                System.out.println("\n==== Huvudmeny ====");
                System.out.println("1. Kundhantering");
                System.out.println("2. Produkthantering");
                System.out.println("3. Orderhantering");
                System.out.println("4. Logga ut");
                System.out.print("Välj alternativ: ");

                choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        CustomerController customerController = new CustomerController(loggedInCustomer);
                        customerController.runMenu();
                        break;
                    case "2":
                        ProductController productController = new ProductController();
                        productController.runMenu();
                        break;
                    case "3":
                        OrderController orderController = new OrderController(loggedInCustomer);
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