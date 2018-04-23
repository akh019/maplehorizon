package server.life;

import java.util.concurrent.ScheduledFuture;

import server.TimerManager;
import client.MapleCharacter;
import java.sql.SQLException;

public class MapleMonsterPet extends MapleMonster {
	
	private MapleCharacter owner;
	private int level, gender;
	private String name;
	private ScheduledFuture<?> actiontask;
	
	public MapleMonsterPet(int id, MapleCharacter owner, int level, int gender, String nickname) {
		super(MapleLifeFactory.getMonster(id));
		this.owner = owner;
		this.level = level;
		this.gender = gender;
		this.name = nickname.equals("") ? getName() : nickname;
		recalcMonsterPetStats();
	}
	
	public void recalcMonsterPetStats() {
		MapleMonsterStats stats = new MapleMonsterStats();
		stats.setName(name);
		stats.setHp((int) (getDefaultStats().getPADamage() + (getDefaultStats().getHp() / getDefaultStats().getLevel() * level * (isMale() ? 1.3 : 0.9))));
		stats.setMp((int) (getDefaultStats().getMADamage() + (getDefaultStats().getMp() / getDefaultStats().getLevel() * level * (isMale() ? 0.9 : 1.3))));
		stats.setPADamage((int) (getDefaultStats().getPADamage() / getDefaultStats().getLevel() * level * (isMale() ? 1.25 : 0.8)));
		stats.setMADamage((int) (getDefaultStats().getMADamage() / getDefaultStats().getLevel() * level * (isMale() ? 0.8 : 1.25)));
		stats.setLevel(level);
		setOverrideStat(stats);
	}
	
	public void spawnMonsterPet(MapleMonsterPet pet, boolean heal) {
		recalcMonsterPetStats();
		owner.getMap().spawnFakeMonsterOnGroundBelow(this, owner.getPosition());
		switchController(owner, true);
		if (heal)
			pet.heal(getHp(), getMp());
		
		doAction();
	}
	
	final private void doAction() {
		final MapleMonsterPet pet = this;
		actiontask = TimerManager.getInstance().register(new Runnable() {
			@Override
			public void run() {
				// TODO;
			}
		}, 1000, 1000);
	}
	
	public void recallMonsterPet(MapleMonster pet, boolean remove) throws SQLException {
		actiontask.cancel(false);
		actiontask = null;
		if (remove)
			getMap().killMonster(pet, null, false);
	}
	
	public void levelUp() {
		level ++;
		recalcMonsterPetStats();
		// TODO: Evolution?
	}
	
	public String getNickname() {
		return name;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getGender() {
		return gender;
	}
	
	public boolean isMale() {
		return gender == 0;
	}
	
}
