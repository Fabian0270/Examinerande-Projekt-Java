package Product;

public class Product {

    private int productId;
    private String name;
    private double price;
    private int quantity;
    private String categoryName;
    private String description;

    public Product(int productId, String name, double price, int quantity, String categoryName, String description) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categoryName = categoryName;
        this.description = description;
    }

    public Product(String name, double price, int quantity, String categoryName) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categoryName = categoryName;
    }

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                (categoryName != null ? ", category='" + categoryName + '\'' : "") +
                '}';
    }
}