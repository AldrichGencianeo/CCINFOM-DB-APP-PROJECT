public class MerchSalesReport {
    private int merchandiseID;
    private String name;
    private int sold;
    private double revenue;
    private int remainingStock;

    public MerchSalesReport(int merchandiseID, String name, int sold, double revenue, int remainingStock) {
        this.merchandiseID = merchandiseID;
        this.name = name;
        this.sold = sold;
        this.revenue = revenue;
        this.remainingStock = remainingStock;
    }

    public int getMerchandiseID() {
        return merchandiseID;
    }

    public String getName() {
        return name;
    }

    public int getSold() {
        return sold;
    }

    public double getRevenue() {
        return revenue;
    }

    public int getRemainingStock() {
        return remainingStock;
    }
}
