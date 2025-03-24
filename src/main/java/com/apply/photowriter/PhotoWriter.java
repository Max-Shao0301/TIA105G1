package com.apply.photowriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Insert Photo to DB

public class PhotoWriter {
    public static void main(String[] args) {
        Connection con = null;
        PreparedStatement ps = null;
        InputStream is = null;
        String url = "jdbc:mysql://localhost:3306/pet?serverTimezone=Asia/Taipei";
        String user = "root";
        String password = "123456";
        String sql = "UPDATE apply set license = ? where apply_id = ?";
        String license = "src/main/resources/static/images/license";

        int count = 1;
        try{
            con = DriverManager.getConnection(url, user, password);
            ps = con.prepareStatement(sql);
            File[] photoFiles = new File(license).listFiles();
            for (File files : photoFiles) {
                is = new FileInputStream(files);
                ps = con.prepareStatement(sql);
                ps.setBinaryStream(1, is);
                ps.setInt(2, count);
                ps.executeUpdate();
                count++;
            }
            is.close();
            ps.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
