/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author xerox19
 */
public class DBworks {
   
      private Connection con;
      private ResultSet rs;
      private Statement st;
      
    public DBworks()
    {
        try 
        {
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/chattest","root","");
            
            
        } catch (Exception e) {
            System.out.println("Error"+e);
        }
    }
    
    
    
    public int reg(String username,String password) throws SQLException
    {
        int count=0;
        String query1="select * from userinfo where username='"+username+"'";
        st=con.createStatement();
        rs=st.executeQuery(query1);
        while(rs.next())
        {
            count+=1;
        }
        
        if(count>0)
        {
            return 0;
        }
        else
        {
           String query2="INSERT INTO userinfo (username,password) VALUES ('"+username+"', '"+password+"')";
           st.executeUpdate(query2);
           st.close();
           return 1;
        }
        
        
    }
    
    public int login(String username,String password) throws SQLException
    {
        int count=0;
        String query1="select * from userinfo where username='"+username+"' and password='"+password+"'";
        st=con.createStatement();
        rs=st.executeQuery(query1);
        while(rs.next())
        {
            count+=1;
        }
        st.close();
        
        if(count>0)
        {
            return 1;
        }
        else
        {
           return 0;
        }
        
        
    }
    
    
    
      
   
    
}
