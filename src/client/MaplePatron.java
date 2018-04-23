package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.DatabaseConnection;

public class MaplePatron {
	private MapleCharacter player;
	private long expiration = -1;
	
	public MaplePatron(MapleCharacter player) {
		this.player = player;
		Connection con = DatabaseConnection.getConnection();
        try{
        	PreparedStatement ps = con.prepareStatement("SELECT expiration FROM patrons WHERE accountid = ?");
        	ps.setInt(1, player.getAccountID());
        	ResultSet rs = ps.executeQuery();
            if(rs.next()){
            	expiration = rs.getLong("expiration");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
	}
	
	public void savePatronToDb(boolean revoke) {
		Connection con = DatabaseConnection.getConnection();
		if (revoke)
			expiration = -1;
		try {
			player.deleteWhereAccountId(con, "DELETE FROM patrons WHERE accountid = ?");
			if (!revoke) {
				PreparedStatement ps = con.prepareStatement("INSERT INTO patrons (accountid, expiration) VALUES (?, ?)");
				ps.setInt(1, player.getAccountID());
				ps.setLong(2, expiration);
				ps.executeUpdate();
			}
		} catch (SQLException e){
            e.printStackTrace();
            System.out.println(e);
        }
	}
	
	public long getExpiration() {
		return expiration;
	}
	
	public void addDays(long days) {
		long time = days * 24L * 60L * 60L * 1000L;
		if (player.isPatron())
			expiration += time;
		else
			expiration = System.currentTimeMillis() + time;
		
		player.message(String.format("You have gained %s days of Patron Status.", days));
	}
}
