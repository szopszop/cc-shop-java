package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.ProductCategory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

public class OrderDaoJdbc implements OrderDao {

    private DataSource dataSource;

    public OrderDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Order createNewOrder(int userId) {
        Order order = new Order(userId);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO  orders (customer_id, status, amount, first_name, last_name, email, address, address2, country, city, zip) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, order.getUserId());
            statement.setString(2, order.getOrderStatus().getName());
            statement.setBigDecimal(3, order.getAmount());
            statement.setString(4, order.getFirstName());
            statement.setString(5, order.getLastName());
            statement.setString(6, order.getEmail());
            statement.setString(7, order.getAddress());
            statement.setString(8, order.getAddress2());
            statement.setString(9, order.getCountry());
            statement.setString(10, order.getCity());
            statement.setString(11, order.getZip());

            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next(); // Read next returned value - in ths case the first one. See ResultSet docs for more info
            order.setId(resultSet.getInt(1));
        } catch (SQLException throwables) {
            throw new RuntimeException("Error while adding new Author", throwables);
        }
        return order;
    }

    @Override
    public Order findByUserId(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT customer_id, status, amount, first_name, last_name, email, address, address2, country, city, zip FROM orders WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            int customerId = resultSet.getInt(2);
            Order.OrderStatus status = Order.OrderStatus.valueOf(resultSet.getString(3));
            BigDecimal amount = resultSet.getBigDecimal(4);

            Order order = new Order(customerId);
            order.setOrderStatus(status);
            order.setAmount(amount);
            order.setId(userId);
            order.setFirstName(resultSet.getString(5));
            order.setLastName(resultSet.getString(6));
            order.setEmail(resultSet.getString(7));
            order.setAddress(resultSet.getString(8));
            order.setAddress2(resultSet.getString(9));
            order.setCountry(resultSet.getString(10));
            order.setCity(resultSet.getString(11));
            order.setZip(resultSet.getString(12));
            return order;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}