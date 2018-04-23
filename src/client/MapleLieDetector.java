package client;

import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import scripting.liedetector.LieDetectorManager;
import server.TimerManager;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleLieDetector {
	
	private MapleCharacter player, tester;
	private int attempt = 0;
	private String answer = "";
	private boolean inProgress = false;
	private ScheduledFuture<?> timer;
	
	public MapleLieDetector(MapleCharacter player) {
		this.player = player;
		reset();
	}
	
	public void startLieDetector(final MapleCharacter tester) {
		final Pair<String, byte[]> captcha = LieDetectorManager.getRandomCaptcha();
		byte[] image = captcha.getRight();
		this.answer = captcha.getLeft();
		this.tester = tester;
		this.inProgress = true;
		this.attempt ++;
		
		if (timer != null) {
			timer.cancel(true);
			timer = null;
		}
		
		player.announce(MaplePacketCreator.sendLieDetector(image));
		timer = TimerManager.getInstance().register(new Runnable() {
			@Override
			public void run() {
				if (inProgress = true && player != null) {
					if (tester != null && tester != player) {
						tester.message("The user has failed the Lie Detector Test. You'll be awarded 10,000 mesos for reporting the user.");
						tester.gainMeso(10000, true, true, true);
					}
                                    try {
                                        player.changeMap(player.getMap().getReturnMap());
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MapleLieDetector.class.getName()).log(Level.SEVERE, null, ex);
                                    }
					player.announce(MaplePacketCreator.LieDetectorResponse((byte) 7, (byte) 4));
					player.announce(MaplePacketCreator.sendPolice("The Lie Detector Test confirms that you have been botting.\r\nRepeated failure of the test will result in game restrictions."));
					reset();
				}
			}
		}, 60000, 60000);
	}
	
	public MapleCharacter getTester() {
		return tester;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public int getAttempt() {
		return attempt;
	}
	
	public boolean inProgress() {
		return inProgress;
	}
	
	public void reset() {
		attempt = 0;
		answer = "";
		inProgress = false;
		if (timer != null) {
			timer.cancel(true);
			timer = null;
		}
	}

}
