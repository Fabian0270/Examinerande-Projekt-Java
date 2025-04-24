package Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CustomerController {

    Customer loggedInCustomer;
    CustomerService customerService = new CustomerService(loggedInCustomer);

    public CustomerController(Customer customer){
        loggedInCustomer = customer;
    }

    public void runMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Hämta alla kunder");
        System.out.println("2. Hämta en kund efter id");
        System.out.println("3. Lägg till kund");
        String select = scanner.nextLine();
        switch (select) {
            case "1":
                ArrayList<Customer> customers = customerService.getAllCustomers();
                for(Customer c : customers){
                    System.out.println("KundId: " + c.getCustomerId());
                    System.out.println("Namn: " + c.getName());
                    System.out.println("Email: " + c.getEmail());
                }
            case "2":
                System.out.println("Ange id:");
                int id = scanner.nextInt();
                Customer customer = customerService.getCustomerById(id);
                System.out.println(customer.getName());
            case "3":
                try {
                    customerService.addCustomer("Exempelnamn", "tele", "mejl", "Hemma", "hemligt");
                } catch (SQLException e){
                    System.out.println(e.getMessage());
                    System.out.println("Someone already has that email-address");
                }
                case "4":
                System.out.println("Ange email:");
                String email = scanner.nextLine();
                    if(!Pattern.matches("^[^@\\s]+@[^@\\s\\.]+\\.[^@\\s]+$", email)){
                        System.out.println("Invalid email format");
                        return;
                    }else if(email.trim().isEmpty()){
                        System.out.println("Du måste ange något");
                    }
                System.out.println("Ange id:");
                int customerId = scanner.nextInt();
                customerService.updateCustomerEmail(email, customerId);
        }

    }

}
