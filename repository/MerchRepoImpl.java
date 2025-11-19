package repository;

import model.Category;
import model.Merchandise;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MerchRepoImpl implements MerchandiseRepo {
    private final Connection connection;

    public MerchRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Merchandise findByID(int merchandiseID) {
        String sql = "SELECT * FROM merchandise WHERE merchandiseID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, merchandiseID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Category category = Category.valueOf(rs.getString("category"));
                return new Merchandise(
                        rs.getInt("merchandiseID"),
                        rs.getString("merchandiseName"),
                        category,
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Merchandise> findAll() {
        List<Merchandise> list = new ArrayList<>();
        String sql = "SELECT * FROM merchandise";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Category category = Category.valueOf(rs.getString("category"));
                list.add(new Merchandise(
                        rs.getInt("merchandiseID"),
                        rs.getString("merchandiseName"),
                        category,
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}