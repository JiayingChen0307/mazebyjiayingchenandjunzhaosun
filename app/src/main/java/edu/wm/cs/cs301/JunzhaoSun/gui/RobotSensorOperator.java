package edu.wm.cs.cs301.JunzhaoSun.gui;

import edu.wm.cs.cs301.JunzhaoSun.gui.Robot.Direction;

/**
 * Execute the failing/repairing process when the corresponding sensor button is clicked
 * @author jenny
 *
 */
public class RobotSensorOperator implements Runnable {
	
	Robot robot;
	Direction d;
	
	public RobotSensorOperator(Robot robot, Direction d) {
		this.robot = robot;
		this.d = d;
	}
	
	@Override
	public void run() {
		while(true) {
		if (robot.hasOperationalSensor(d)) robot.triggerSensorFailure(d);
		else robot.repairFailedSensor(d);
		
		try {
			Thread.sleep(3000);
		} catch(InterruptedException e) {
			robot.repairFailedSensor(d);
			return;
		}
		}

	}

}
