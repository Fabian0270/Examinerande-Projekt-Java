package Order;

import Customer.Customer;
import Customer.CustomerRepository;
import Product.Product;
import Product.ProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderController {

    private Customer activeCustomer;
    private OrderService orderService;
    private ProductService productService;
    private CustomerRepository customerRepository;

    public OrderController(Customer customer) {
        // Tillåt null-kund och hantera det internt
        this.activeCustomer = customer;
        this.orderService = new OrderService();
        this.productService = new ProductService();
        this.customerRepository = new CustomerRepository();
    }

    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==== Order Menu ====");

            // Om ingen kund är vald, visa alternativ för att välja kund
            if (activeCustomer == null) {
                System.out.println("Ingen kund vald.");
                System.out.println("1. Välj kund");
                System.out.println("2. Återgå till huvudmenyn");
            } else {
                System.out.println("Aktiv kund: " + activeCustomer.getName() + " (ID: " + activeCustomer.getCustomerId() + ")");
                System.out.println("1. Se orderhistorik");
                System.out.println("2. Skapa ny order");
                System.out.println("3. Byt kund");
                System.out.println("4. Återgå till huvudmenyn");
            }

            System.out.print("Välj ett alternativ: ");
            String choice = scanner.nextLine();

            try {
                if (activeCustomer == null) {
                    // Meny när ingen kund är vald
                    switch (choice) {
                        case "1":
                            selectCustomer(scanner);
                            break;
                        case "2":
                            running = false;
                            break;
                        default:
                            System.out.println("Ogiltigt val. Försök igen.");
                            break;
                    }
                } else {
                    // Meny när en kund är vald
                    switch (choice) {
                        case "1":
                            showOrderHistory();
                            break;
                        case "2":
                            createNewOrder(scanner);
                            break;
                        case "3":
                            selectCustomer(scanner);
                            break;
                        case "4":
                            running = false;
                            break;
                        default:
                            System.out.println("Ogiltigt val. Försök igen.");
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Ett fel uppstod: " + e.getMessage());
            }
        }
    }

    private void selectCustomer(Scanner scanner) {
        try {
            // Visa alla kunder
            System.out.println("\n==== Välj kund ====");
            System.out.println("1. Visa alla kunder");
            System.out.println("2. Sök kund efter ID");
            System.out.print("Välj ett alternativ: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // Visa alla kunder för att välja
                    List<Customer> customers = customerRepository.getAll();
                    if (customers.isEmpty()) {
                        System.out.println("Inga kunder finns registrerade.");
                        return;
                    }

                    System.out.println("\n==== Tillgängliga kunder ====");
                    for (Customer c : customers) {
                        System.out.println("ID: " + c.getCustomerId() + " - " + c.getName() + " (" + c.getEmail() + ")");
                    }

                    System.out.print("\nAnge kund-ID för den kund du vill välja: ");
                    try {
                        int customerId = Integer.parseInt(scanner.nextLine());
                        Customer selectedCustomer = customerRepository.getCustomerById(customerId);

                        if (selectedCustomer != null) {
                            this.activeCustomer = selectedCustomer;
                            System.out.println("Kund vald: " + selectedCustomer.getName());
                        } else {
                            System.out.println("Ingen kund hittades med ID: " + customerId);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ogiltigt ID. Ange ett heltal.");
                    }
                    break;

                case "2":
                    // Sök efter specifikt kund-ID
                    System.out.print("Ange kund-ID: ");
                    try {
                        int customerId = Integer.parseInt(scanner.nextLine());
                        Customer customer = customerRepository.getCustomerById(customerId);

                        if (customer != null) {
                            this.activeCustomer = customer;
                            System.out.println("Kund vald: " + customer.getName());
                        } else {
                            System.out.println("Ingen kund hittades med ID: " + customerId);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ogiltigt ID. Ange ett heltal.");
                    }
                    break;

                default:
                    System.out.println("Ogiltigt val.");
                    break;
            }

        } catch (SQLException e) {
            System.out.println("Ett fel uppstod vid hämtning av kunder: " + e.getMessage());
        }
    }

    private void showOrderHistory() {
        if (activeCustomer == null) {
            System.out.println("Ingen kund är vald. Välj en kund först.");
            return;
        }

        try {
            List<Order> orders = orderService.getOrderHistoryForCustomer(activeCustomer.getCustomerId());

            if (orders.isEmpty()) {
                System.out.println("Kunden har inga tidigare ordrar.");
                return;
            }

            System.out.println("\n==== Orderhistorik för " + activeCustomer.getName() + " ====");
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
            System.out.println("Ett fel uppstod vid hämtning av orderhistorik: " + e.getMessage());
        }
    }

    private void createNewOrder(Scanner scanner) {
        if (activeCustomer == null) {
            System.out.println("Ingen kund är vald. Välj en kund först.");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        boolean addingProducts = true;

        try {
            while (addingProducts) {
                // Visa alla produkter
                List<Product> products = productService.getAllProducts();

                if (products.isEmpty()) {
                    System.out.println("Det finns inga produkter tillgängliga för närvarande.");
                    return;
                }

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

                        if (quantity <= 0) {
                            System.out.println("Antalet måste vara större än 0.");
                            continue;
                        }

                        // Validera kvantitet
                        try {
                            orderService.validateStock(selectedProduct.getProductId(), quantity);

                            OrderItem item = new OrderItem(selectedProduct, quantity);
                            orderItems.add(item);

                            System.out.println(selectedProduct.getName() + " lades till i ordern.");

                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Ogiltigt antal. Ange ett heltal.");
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
                try {
                    Order order = orderService.createOrder(activeCustomer, orderItems);

                    System.out.println("\nOrdern har skapats!");
                    System.out.println("Order #" + order.getOrderId());
                    System.out.println("Totalt: " + order.getTotalAmount() + " kr");

                    System.out.println("\nOrderade produkter:");
                    for (OrderItem item : order.getOrderItems()) {
                        System.out.printf("- %s: %d st x %.2f kr = %.2f kr\n",
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPriceAtPurchase(),
                                item.getSubtotal()
                        );
                    }
                } catch (SQLException | IllegalArgumentException e) {
                    System.out.println("Fel vid skapande av order: " + e.getMessage());
                }
            } else {
                System.out.println("Ordern avbröts - inga produkter valda.");
            }

        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }
}