package Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerService {

    private Customer loggedInCustomer;
    private CustomerRepository customerRepository;

    public CustomerService(Customer customer){
        this.loggedInCustomer = customer;
        this.customerRepository = new CustomerRepository();
    }

    public ArrayList<Customer> getAllCustomers() throws SQLException {
        try {
            return customerRepository.getAll();
        } catch (SQLException e) {
            throw new SQLException("Kunde inte hämta kunder: " + e.getMessage());
        }
    }

    public Customer getCustomerById(int id) throws SQLException {
        try {
            return customerRepository.getCustomerById(id);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte hämta kund: " + e.getMessage());
        }
    }

    public void addCustomer(String name, String phone, String email, String address, String password) throws SQLException {
        try {
            // Validera indata
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Namnet får inte vara tomt");
            }

            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("E-postadressen får inte vara tom");
            }

            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("Lösenordet måste vara minst 6 tecken");
            }

            customerRepository.addCustomer(name, phone, email, address, password);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte lägga till kund: " + e.getMessage());
        }
    }

    public void updateCustomerEmail(String email, int customerId) throws SQLException {
        try {
            // Validera e-postadressen
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("E-postadressen får inte vara tom");
            }

            customerRepository.updateCustomerEmail(email, customerId);
        } catch (SQLException e) {
            throw new SQLException("Kunde inte uppdatera kundens e-postadress: " + e.getMessage());
        }
    }
}