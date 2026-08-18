package com.example.DataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = System.getenv().getOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/rezervasyonotomasyonu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "123456");


    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Başarıyla Yüklendi");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver Bulunamadı!");
            throw new RuntimeException("JDBC Driver Hatası", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Veritabanı bağlantısı başarılı!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Veritabanı Bağlantısı Kapatıldı");
            } catch (SQLException e) {
                System.err.println("Bağlantı Kapatma Hatası: " + e.getMessage());
            }
        }
    }
}