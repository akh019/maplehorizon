package bots;

import java.util.List;

import server.movement.LifeMovementFragment;

public class MaplePhysics {
	public static final double DownJumpMultiplier = 0.35355339;
    public static final double Epsilon = 0.00001;
    public static final double FallSpeed = 670;
    public static final double FloatCoefficient = 0.01;
    public static final double FloatDrag1 = 100000;
    public static final double FloatDrag2 = 10000;
    public static final double FloatMultiplier = 0.0008928571428571428;
    public static final double FlyForce = 120000;
    public static final double FlyJumpDec = 0.35;
    public static final double FlySpeed = 200;
    public static final double GravityAcc = 2000;
    public static final double JumpSpeed = 555;
    public static final double MaxFriction = 2;
    public static final double MaxLandSpeed = 162.5;
    public static final double MinFriction = 0.05;
    public static final double ShoeFlyAcc = 0;
    public static final double ShoeFlySpeed = 0;
    public static final double ShoeMass = 100;
    public static final double ShoeSwimAcc = 1;
    public static final double ShoeSwimSpeedH = 1;
    public static final double ShoeSwimSpeedV = 1;
    public static final double ShoeWalkAcc = 1;
    public static final double ShoeWalkJump = 1.2;
    public static final double ShoeWalkSlant = 0.9;
    public static final double ShoeWalkSpeed = 1.4;
    public static final double SlipForce = 60000;
    public static final double SlipSpeed = 120;
    public static final double SwimForce = 120000;
    public static final double SwimJump = 700;
    public static final double SwimSpeed = 140;
    public static final double SwimSpeedDec = 0.9;
    public static final double WalkDrag = 80000;
    public static final double WalkForce = 140000;
    public static final double WalkSpeed = 125;
    
    public List<LifeMovementFragment> getJump() {
    	
    	return null;
    }
}
