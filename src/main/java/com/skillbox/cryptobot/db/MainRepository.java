package com.skillbox.cryptobot.db;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public class MainRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initialization(){
        String sql = "CREATE TABLE IF NOT EXISTS Subscribers (" +
                "ID uuid PRIMARY KEY," +
                "Telegram_ID bigint UNIQUE," +
                "Chat_ID bigint," +
                "Price numeric(30, 2)," +
                "Notified_At timestamp" +
                ");";
        jdbcTemplate.execute(sql);
        sql = "CREATE INDEX ON Subscribers (Price, Notified_At);";
        jdbcTemplate.execute(sql);
    }

    public void registerUser(Long user, Long chat){
        String sql = "INSERT INTO Subscribers (ID, Telegram_ID, Chat_ID) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, UUID.randomUUID(), user, chat);
    }

    public void setPrice(Long id, BigDecimal price){
        String sql = "UPDATE Subscribers SET Notified_At = current_timestamp, Price = ? WHERE Telegram_ID = ?";
        jdbcTemplate.update(sql, price, id);
    }

    public BigDecimal getPrice(Long id){
        String sql = "SELECT Price FROM Subscribers WHERE Telegram_ID = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
    }

    public List<Long> notificationList(double price, int value, String unit){
        String sql = "SELECT Chat_ID FROM Subscribers " +
                "WHERE Price <= ? " +
                "AND Notified_At + (? * interval '1 " + unit + "') <= current_timestamp";
        var result = jdbcTemplate.queryForList(sql, Long.class, price, value);
        if (!result.isEmpty()) {
            sql = "UPDATE Subscribers " +
                    "SET Notified_At = current_timestamp " +
                    "WHERE Price <= ? " +
                    "AND Notified_At + (? * interval '1 " + unit + "') <= current_timestamp";
            jdbcTemplate.update(sql, price, value);
        }
        return result;
    }
}
