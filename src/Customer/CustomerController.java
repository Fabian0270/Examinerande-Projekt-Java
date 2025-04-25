package Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CustomerController {

    private CustomerService customerService;

    public CustomerController(Customer customer) {
        // Vi ignorerar den inkommande kunden för enkelhetens skull
        this.customerService = new CustomerService(null);
    }

    public void runMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==== Kundhantering ====");
            System.out.println("1. Visa alla kunder");
            System.out.println("2. Visa detaljer för en kund");
            System.out.println("3. Lägg till ny kund");
            System.out.println("4. Uppdatera kundens e-postadress");
            System.out.println("5. Återgå till huvudmenyn");
            System.out.print("Välj alternativ: ");

            String select = scanner.nextLine();

            try {
                switch (select) {
                    case "1":
                        showAllCustomers();
                        break;

                    case "2":
                        getCustomerDetails(scanner);
                        break;

                    case "3":
                        addNewCustomer(scanner);
                        break;

                    case "4":
                        updateCustomerEmail(scanner);
                        break;

                    case "5":
                        running = false;
                        break;

                    default:
                        System.out.println("Ogiltigt val. Försök igen.");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Databasfel: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ett fel inträffade: " + e.getMessage());
            }
        }
    }

    private void showAllCustomers() throws SQLException {
        ArrayList<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("Inga kunder hittades.");
        } else {
            System.out.println("\n=== Alla kunder ===");
            for(Customer c : customers){
                System.out.println("KundId: " + c.getCustomerId());
                System.out.println("Namn: " + c.getName());
                System.out.println("Email: " + c.getEmail());
                System.out.println("---------------------");
            }
        }
    }

    private void getCustomerDetails(Scanner scanner) throws SQLException {
        System.out.print("Ange kund-ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Customer customer = customerService.getCustomerById(id);

            if (customer != null) {
                System.out.println("\n=== Kunddetaljer ===");
                System.out.println("KundId: " + customer.getCustomerId());
                System.out.println("Namn: " + customer.getName());
                System.out.println("Email: " + customer.getEmail());
            } else {
                System.out.println("Ingen kund hittades med ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID. Ange ett heltal.");
        }
    }

    private void addNewCustomer(Scanner scanner) throws SQLException {
        System.out.println("\n=== Lägg till ny kund ===");

        System.out.print("Namn: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Namnet får inte vara tomt.");
            return;
        }

        System.out.print("Email: ");
        String email = scanner.nextLine();
        if (email.trim().isEmpty()) {
            System.out.println("E-postadressen får inte vara tom.");
            return;
        }
        if (!Pattern.matches("^[^@\\s]+@[^@\\s\\.]+\\.[^@\\s]+$", email)) {
            System.out.println("Ogiltig e-postadress format.");
            return;
        }

        System.out.print("Telefon: ");
        String phone = scanner.nextLine();

        System.out.print("Adress: ");
        String address = scanner.nextLine();

        System.out.print("Lösenord: ");
        String password = scanner.nextLine();
        if (password.length() < 6) {
            System.out.println("Lösenordet måste vara minst 6 tecken.");
            return;
        }

        try {
            customerService.addCustomer(name, phone, email, address, password);
            System.out.println("Kunden har lagts till!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("En kund med den e-postadressen finns redan.");
        }
    }

    private void updateCustomerEmail(Scanner scanner) throws SQLException {
        System.out.println("\n=== Uppdatera kundens e-postadress ===");

        System.out.print("Ange kundens ID: ");
        try {
            int customerId = Integer.parseInt(scanner.nextLine());

            // Verifiera att kunden finns
            Customer customer = customerService.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Ingen kund hittades med ID: " + customerId);
                return;
            }

            System.out.println("Nuvarande e-post för " + customer.getName() + ": " + customer.getEmail());

            System.out.print("Ange ny e-postadress: ");
            String email = scanner.nextLine();

            if (email.trim().isEmpty()) {
                System.out.println("E-postadressen får inte vara tom.");
                return;
            }

            if (!Pattern.matches("^[^@\\s]+@[^@\\s\\.]+\\.[^@\\s]+$", email)) {
                System.out.println("Ogiltig e-postadress format.");
                return;
            }

            customerService.updateCustomerEmail(email, customerId);
            System.out.println("E-postadressen har uppdaterats!");

        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID. Ange ett heltal.");
        } catch (SQLException e) {
            System.out.println("Kunde inte uppdatera e-postadressen: " + e.getMessage());
        }
    }
}