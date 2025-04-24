package Customer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerRepository {

    public static final String URL = "jdbc:sqlite:webbutiken.db";

    public ArrayList<Customer> getAll() throws SQLException {

        ArrayList<Customer> customers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {

            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("customer_id"),
                                                    rs.getString("name"),
                                                    rs.getString("email"));
                customers.add(customer);
                customer.introduce();
            }
        }
        return customers;
    }

    public Customer getCustomerById(int customerId) throws SQLException {

        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            ResultSet rs = pstmt.executeQuery();

            return new Customer(customerId, rs.getString("name"), rs.getString("email"));
        }
    }

    public Customer login(String email, String password) throws SQLException {

        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();

            Customer customer = new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("email"));

            return customer;
            //if customer.getPassword equals password (argumentet som skickades till metoden
            //om detta stämmer: return customer
            //annars invalid login
        }
    }

    public void addCustomer(String name, String phone, String email, String address, String password) throws SQLException {

        String sql = "INSERT INTO customers (name, email, phone, address, password) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, password);

            pstmt.executeUpdate();
        }
    }

    public void updateCustomerEmail(String email, int customerId) throws SQLException {

        String sql = "UPDATE customers\n" +
                "   SET \n" +
                "       email = ?\n" +
                " WHERE customer_id = ?;";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, customerId);

            pstmt.executeUpdate();
        }
        Customer customer = getCustomerById(customerId);
        System.out.println("Your new email is: " + customer.getEmail());
    }
}
