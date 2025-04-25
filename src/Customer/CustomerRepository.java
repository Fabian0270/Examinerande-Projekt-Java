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
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av kunder: " + e.getMessage());
        }

        return customers;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        customerId,
                        rs.getString("name"),
                        rs.getString("email")
                );
            } else {
                return null; // Ingen kund hittades med det angivna ID
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid hämtning av kund: " + e.getMessage());
        }
    }

    public Customer login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            } else {
                throw new SQLException("Fel användarnamn eller lösenord");
            }
        } catch (SQLException e) {
            throw new SQLException("Fel vid inloggning: " + e.getMessage());
        }
    }

    public void addCustomer(String name, String phone, String email, String address, String password) throws SQLException {
        // Kontrollera först om e-postadressen redan används
        String checkSql = "SELECT COUNT(*) FROM customers WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("En kund med denna e-postadress finns redan");
            }
        }

        // Lägg till kunden om e-postadressen är unik
        String sql = "INSERT INTO customers (name, phone, email, address, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, password);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Fel vid tillägg av kund: " + e.getMessage());
        }
    }

    public void updateCustomerEmail(String email, int customerId) throws SQLException {
        String sql = "UPDATE customers SET email = ? WHERE customer_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, customerId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Ingen kund hittades med ID: " + customerId);
            }

            Customer customer = getCustomerById(customerId);
            System.out.println("Din nya e-postadress är: " + customer.getEmail());
        } catch (SQLException e) {
            throw new SQLException("Fel vid uppdatering av e-postadress: " + e.getMessage());
        }
    }
}