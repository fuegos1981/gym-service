package com.gym.automation.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@Component
public class MySqlRepository {

    public final static String GET_USER_BY_USERNAME = "select * from user where username= ?";
    public final static String INSERT_USER = "INSERT INTO user (first_name,last_name, username, password, is_active) VALUES (?,?,?,?,?)";
    public final static String INSERT_TRAINEE = "INSERT INTO trainee (user_id,date_of_birth, address) VALUES (?,?,?)";
    public final static String INSERT_TRAINING_TYPE = "INSERT INTO training_type (name) VALUES (?)";
    public final static String INSERT_TRAINER = "INSERT INTO trainer (user_id,specialization_id) VALUES (?,?)";

    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String DB_USER;

    @Value("${spring.datasource.password}")
    private String DB_PASSWORD;

    public Long read(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            addFilters(preparedStatement, new String[]{username});
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next() ? rs.getLong(1) : -1;
        }

    }

    public Long save(String query, Object... filters) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            addFilters(preparedStatement, filters);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return -1L;
        }
    }

    private void addFilters(PreparedStatement preparedStatement, Object[] filters) throws SQLException {
        if (filters != null) {
            int step = 1;
            for (Object obj : filters) {
                if (obj instanceof String) {
                    preparedStatement.setString(step, (String) obj);
                } else if (obj instanceof LocalDate localDate) {
                    preparedStatement.setDate(step, java.sql.Date.valueOf(localDate));
                } else if (obj instanceof Boolean) {
                    preparedStatement.setBoolean(step, (Boolean) obj);
                } else if (obj instanceof Double) {
                    preparedStatement.setDouble(step, (Double) obj);
                } else if (obj instanceof Long) {
                    preparedStatement.setLong(step, (Long) obj);
                } else {
                    preparedStatement.setInt(step, (Integer) obj);
                }
                step++;
            }
        }
    }
}
