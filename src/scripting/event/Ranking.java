/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scripting.event;




import client.MapleCharacter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import tools.DatabaseConnection;

/**
 *
 * @author Administrator
 */
public class Ranking {
    
    public static ResultSet getRbrank(){
        try{
        PreparedStatement ps;
        Connection con = DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, rebirths FROM characters ORDER BY rebirths desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
    public static ResultSet getCurrank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, currency FROM characters ORDER BY currency desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
    public static ResultSet getEprank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, eventpoints FROM characters ORDER BY eventpoints desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getFamerank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, fame FROM characters ORDER BY fame desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getErprank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, erp FROM characters ORDER BY erp desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getOmokrank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, omokwins FROM characters ORDER BY omokwins desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getOmscorerank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, omokpoints FROM characters ORDER BY omokpoints desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getFishptsrank(){
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, fishpoints FROM characters ORDER BY fishpoints desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getFishexprank(){
         
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT name, fishexp FROM characters ORDER BY fishexp desc LIMIT 10");
        return ps.executeQuery();
        } catch(SQLException sqlexc){
            return null;
        }
    
    }
     public static ResultSet getJqrank(int id){
         
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT player, time FROM jqtimes WHERE jqid = ? AND month = ? AND gm = ? ORDER BY time asc LIMIT 10");
        ps.setInt(1, id);
        if(Calendar.getInstance().get(Calendar.MONTH)+1 == 13)
             ps.setInt(2, 1);
        else 
          ps.setInt(2, Calendar.getInstance().get(Calendar.MONTH)+1);
        ps.setInt(3,0);
        return ps.executeQuery();
        } catch(SQLException sqlexc){
             System.out.print("Error inserting jqrank: " + sqlexc);
            return null;
        }
    
    }
      public static ResultSet getJqpersrank(int id, MapleCharacter player){
         
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT time FROM jqtimes WHERE jqid = ? AND player = ?");
        ps.setInt(1, id);
        ps.setString(2, player.getName());
        return ps.executeQuery();
        } catch(SQLException sqlexc){
              System.out.print("Error inserting jq personal rank: " + sqlexc);
            return null;
        }
    
    }
      public static ResultSet getJqptsrank(){
         
        try{
        PreparedStatement ps;
        Connection con =  DatabaseConnection.getConnection();
         ps = con.prepareStatement("SELECT name, jqpoints FROM characters ORDER BY jqpoints desc LIMIT 10");
       
        return ps.executeQuery();
        } catch(SQLException sqlexc){
              System.out.print("Error inserting jq point rank: " + sqlexc);
            return null;
        }
    
    }
    
}
