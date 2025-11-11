enum Category {
    Clothing, Collectible, Music, Accessory
}

public class Merchandise {
    private int merchandiseID;
    private String merchandiseName;
    private Category category;
    private double price;
    private int stock;

    // Constructor
    public Merchandise() {}

    public Merchandise(int merchandiseID, String merchandiseName, Category category, double price, int stock) {
        this.merchandiseID = merchandiseID;
        this.merchandiseName = merchandiseName;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Getters
    public int getMerchandiseID() {
        return merchandiseID;
    }

    public String getMerchandiseName() {
        return merchandiseName;
    }

    public Category getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // Setters
    public void setMerchandiseID(int merchandiseID) {
        this.merchandiseID = merchandiseID;
    }

    public void setMerchandiseName(String name) {
        this.merchandiseName = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return String.format("Merchandise[ID=%d, Name=%s, Category=%s, Price=%.2f, Stock=%d]",
                merchandiseID, merchandiseName, category, price, stock);
    }
}
