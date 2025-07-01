package com.themoneywallet.config.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.protocol.Resultset;

public class MyFirstJdbc {
    
    public MyFirstJdbc(){
  try {
  DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            String url = "jdbc:mysql://localhost:3306/your_db";
            String user = "root";
            String password = "your_password";

  Connection con = DriverManager.getConnection(url, user, password);

  Statement st = con.createStatement();
  PreparedStatement stt = con.prepareStatement("select * from test where id = ? and name = ?");

  stt.setInt(1, 1);
  stt.setString(0, "hello");
  //stt.executeQuery()
  //stt.executeUpdate()
  ResultSet rs = st.executeQuery("selcet * from test");
    while (rs.next()) {
      System.out.println(rs.getString(1));
    }
    st.close();
    rs.close();

} catch (SQLException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
}
    }
}
