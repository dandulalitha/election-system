// src/main/java/com/example/dao/UserDAO.java
package com.example.dao;

import com.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/user_management?useSSL=false"; // Ensure useSSL=false if not using SSL
    private String jdbcUsername = "root";
    private String jdbcPassword = "your_mysql_password"; // !!! CHANGE THIS !!!

    private static final String INSERT_USERS_SQL = "INSERT INTO users (name, email, phone, role) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_USERS_SQL = "SELECT id, name, email, phone, role FROM users";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT id, name, email, phone, role FROM users WHERE id = ?";
    private static final String UPDATE_USERS_SQL = "UPDATE users SET name = ?, email = ?, phone = ?, role = ? WHERE id = ?";
    private static final String DELETE_USERS_SQL = "DELETE FROM users WHERE id = ?";
    private static final String CHECK_EMAIL_EXISTENCE = "SELECT COUNT(*) FROM users WHERE email = ?";

    protected Connection getConnection() throws SQLException {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL 8.0+
            // For older versions: Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.executeUpdate();
        }
    }

    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_SQL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String role = rs.getString("role");
                users.add(new User(id, name, email, phone, role));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately in a real application
        }
        return users;
    }

    public User selectUser(int id) {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String role = rs.getString("role");
                user = new User(id, name, email, phone, role);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
        return user;
    }

    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getRole());
            statement.setInt(5, user.getId());
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    public boolean isEmailUnique(String email) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_EMAIL_EXISTENCE)) {
            preparedStatement.setString(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // 0 means unique, 1 means exists
            }
        }
        return false; // Default to false if there's an error or no result
    }
}