package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import server.life.MapleMonsterPet;
import tools.DatabaseConnection;

public class MapleMonsterPetList {

	private MapleCharacter owner;
	private List<MapleMonsterPet> monsterPets = new ArrayList<>();
	
	public MapleMonsterPetList(MapleCharacter chr) {
		owner = chr;
		getAllMonsterPets();
	}
	
	public List<MapleMonsterPet> getAllMonsterPets() {
		if (monsterPets.isEmpty()) {
			try {
				Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM monsterpets WHERE characterid = ?");
				ps.setInt(1, owner.getId());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					monsterPets.add(new MapleMonsterPet(rs.getInt("monsterid"), owner, rs.getInt("level"), rs.getInt("gender"), rs.getString("nickname")));
				}
				ps.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.unmodifiableList(monsterPets);
	}
	
	public void saveAllMonsterPets() {
		try {
			Connection con = DatabaseConnection.getConnection();
			owner.deleteWhereCharacterId(con, "DELETE FROM monsterpets WHERE characterid = ?");
			PreparedStatement ps = con.prepareStatement("INSERT INTO monsterpets (characterid, monsterid, nickname, level, gender) VALUES (?, ?, ?, ?, ?)");
			ps.setInt(1, owner.getId());
			for (MapleMonsterPet mPet : monsterPets) {
				ps.setInt(2, mPet.getId());
				ps.setString(3, mPet.getNickname());
				ps.setInt(4, mPet.getLevel());
				ps.setInt(5, mPet.getGender());
                ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addMonsterPet(int id, int level, int gender) {
		addMonsterPet(id, level, gender, "");
	}
	
	public void addMonsterPet(int id, int level, int gender, String nickname) {
		monsterPets.add(new MapleMonsterPet(id, owner, level, gender, nickname));
	}
}
