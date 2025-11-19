package repository;

import model.MerchReceipt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MerchReceiptRepoImpl implements MerchReceiptRepo {
    private final Connection connection;

    public MerchReceiptRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<MerchReceipt> findByEventID(int eventID) {
        List<MerchReceipt> list = new ArrayList<>();
        String sql = "SELECT * FROM merch_receipt WHERE eventID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new MerchReceipt(
                        rs.getInt("receiptID"),
                        rs.getInt("customerID"),
                        rs.getInt("eventID"),
                        rs.getInt("merchandiseID"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalPrice")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MerchReceipt> findByMonth(int year, int month) {
        List<MerchReceipt> list = new ArrayList<>();
        String sql = "SELECT * FROM merch_receipt WHERE YEAR(purchaseDate) = ? AND MONTH(purchaseDate) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new MerchReceipt(
                        rs.getInt("receiptID"),
                        rs.getInt("customerID"),
                        rs.getInt("eventID"),
                        rs.getInt("merchandiseID"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalPrice")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MerchReceipt> findAll() {
        List<MerchReceipt> list = new ArrayList<>();
        String sql = "SELECT * FROM merch_receipt";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new MerchReceipt(
                        rs.getInt("receiptID"),
                        rs.getInt("customerID"),
                        rs.getInt("eventID"),
                        rs.getInt("merchandiseID"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalPrice")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}