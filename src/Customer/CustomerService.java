package Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerService {

    Customer loggedInCustomer;

    public CustomerService(Customer customer){
        loggedInCustomer = customer;
    }

    CustomerRepository customerRepository = new CustomerRepository();

    public ArrayList<Customer> getAllCustomers() throws SQLException {
        System.out.println("Detta är vårt logiska lager");
        System.out.println("Här ordnar vi med uträkningar och sånt kul");
        return customerRepository.getAll();
    }

    public Customer getCustomerById(int id) throws SQLException {
        return customerRepository.getCustomerById(id);
    }

    public void addCustomer(String name, String phone, String email, String address, String password) throws SQLException {
        customerRepository.addCustomer(name, phone, email, address, password);
    }

    public void updateCustomerEmail(String email, int customerId) throws SQLException {
        customerRepository.updateCustomerEmail(email, customerId);
    }

}
