package model;

public class MerchReceipt {
    private int receiptID;
    private int customerID;
    private int eventID;
    private int merchandiseID;
    private int quantity;
    private double totalPrice;

    public MerchReceipt() {}

    public MerchReceipt(int receiptID, int customerID, int eventID, int merchandiseID,
                        int quantity, double totalPrice) {
        this.receiptID = receiptID;
        this.customerID = customerID;
        this.eventID = eventID;
        this.merchandiseID = merchandiseID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getReceiptID() {
        return receiptID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getEventID() {
        return eventID;
    }

    public int getMerchandiseID() {
        return merchandiseID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setReceiptID(int receiptID) {
        this.receiptID = receiptID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setMerchandiseID(int merchandiseID) {
        this.merchandiseID = merchandiseID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return String.format("Receipt[ID=%d, CustomerID=%d, EventID=%d, MerchID=%d, Qty=%d, Total=%.2f]",
                receiptID, customerID, eventID, merchandiseID, quantity, totalPrice);
    }
}