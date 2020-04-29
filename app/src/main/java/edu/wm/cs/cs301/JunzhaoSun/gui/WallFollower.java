package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.util.Log;

import edu.wm.cs.cs301.JunzhaoSun.generation.Distance;
import edu.wm.cs.cs301.JunzhaoSun.gui.Robot.Direction;
import edu.wm.cs.cs301.JunzhaoSun.gui.Robot.Turn;

/**
 * WallFollower implements RobotDriver interface. It uses distance sensor to detect the forward and left distance
 * and follow the left wall to find the exit.
 * @author Jiaying Chen
 *
 */
public class WallFollower implements RobotDriver{

    private BasicRobot robot;
    private int width;
    private int height;
    private int frontDistance;
    private int leftDistance;

    private boolean stop = false;

    public WallFollower() {
        //to-do
    }

    /**
     * Assigns a robot platform to the driver.
     * The driver uses a robot to perform, this method provides it with this necessary information.
     * @param r robot to operate
     */
    @Override
    public void setRobot(Robot r){
        this.robot = (BasicRobot)r;
    }

    /**
     * Provides the robot driver with information on the dimensions of the 2D maze
     * measured in the number of cells in each direction.
     * @param width of the maze
     * @param height of the maze
     * @precondition 0 <= width, 0 <= height of the maze.
     */
    @Override
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Provides the robot driver with information on the distance to the exit.
     * Only some drivers such as the wizard rely on this information to find the exit.
     * @param distance gives the length of path from current position to the exit.
     * @precondition null != distance, a full functional distance object for the current maze.
     */
    @Override
    public void setDistance(Distance distance) {
        return;
    }

    /**
     * Tells the driver to check its robot for operational sensor.
     * If one or more of the robot's distance sensor become
     * operational again after a repair operation, this method
     * allows to make the robot driver aware of this change
     * and to bring its understanding of which sensors are operational
     * up to date.
     */
    @Override
    public void triggerUpdateSensorInformation() {
        throw new RuntimeException("triggerUpdateSensorInfo:using unimplemented method");
    }

    /**
     * Drives the robot towards the exit given it exists and
     * given the robot's energy supply lasts long enough.
     * @return true if driver successfully reaches the exit, false otherwise
     */

    @Override
    public boolean drive2Exit() throws Exception {
        Boolean hasStopped = false;
        Boolean reachedExit = false;
        while (!stop) {
            try {

                //set out of the maze when the robot is at the exit
                if (reachedExit) {
                    return actionAtExit();
                }
                //case 1: L & F sensors active, don't rotate, follow left wall
                if (robot.hasOperationalSensor(Direction.LEFT)&
                        robot.hasOperationalSensor(Direction.FORWARD)) {

                    leftDistance = robot.distanceToObstacle(Direction.LEFT);
                    //case2: L failed, F active
                }else if (!robot.hasOperationalSensor(Direction.LEFT) &&
                        robot.hasOperationalSensor(Direction.FORWARD)) {

                    robot.rotate(Turn.LEFT);
                    leftDistance = robot.distanceToObstacle(Direction.FORWARD);
                    robot.rotate(Turn.RIGHT);
                    //case 3: L active, F failed
                }else if (robot.hasOperationalSensor(Direction.LEFT) &&
                        !robot.hasOperationalSensor(Direction.FORWARD)){
                    leftDistance = robot.distanceToObstacle(Direction.LEFT);
                    //case 3: L failed, F failed
                }else {
                    //R active,
                    if (robot.hasOperationalSensor(Direction.RIGHT)) {
                        robot.rotate(Turn.AROUND);
                        leftDistance = robot.distanceToObstacle(Direction.RIGHT);
                        robot.rotate(Turn.AROUND);

                        //B active, R failed,
                    }else if (robot.hasOperationalSensor(Direction.BACKWARD)) {
                        robot.rotate(Turn.RIGHT);
                        leftDistance = robot.distanceToObstacle(Direction.BACKWARD);
                        robot.rotate(Turn.LEFT);
                    }//all sensors failed,robot do nothing until some sensor(s) go back to work
                }
                getFrontDistance();
                hasStopped = driveFollowLeftWall();
                if (robot.isAtExit()) {
                    reachedExit = true;
                }
                if (hasStopped) {
                    return false;
                }
                if (robot.hasStopped() || robot.hasWon()) return false;
            }catch(UnsupportedOperationException e) {
                Log.e("unsupportedException for WallFollower", "unsupportedException for WallFollower");
                return drive2Exit();
            }catch(AssertionError e) {
                Log.e("assertionError for WallFollower", "assertionError for WallFollower");
                return false;}
        }

        return false;

    }

    /**
     * Move the robot out of the maze when it is at the exit position.
     * @return When sensor in the exit direction is not operational, recur until the sensor goes back to work
     * @throws Exception when canSeeThrough method throws exception or AssertionError
     */
    private Boolean actionAtExit() throws Exception {
        try {
            //check at what direction the exit is and step out if the maze at that direction
            if (robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
                robot.move(1, false);
                return true;
            }
            if (robot.canSeeThroughTheExitIntoEternity(Direction.LEFT)) {
                robot.rotate(Turn.LEFT);
                robot.move(1, false);
                return true;
            }
            if (robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT)) {
                robot.rotate(Turn.RIGHT);
                robot.move(1, false);
                return true;
            }
            if (robot.canSeeThroughTheExitIntoEternity(Direction.BACKWARD)) {
                robot.rotate(Turn.AROUND);
                robot.move(1, false);
                return true;
            }
        }catch(Exception e) {
            throw new Exception();
        }catch(AssertionError a) {
            throw new AssertionError();
        }
        return actionAtExit();
    }

    /**
     * get the distance in the forward direction
     * @return
     * @throws Exception when relevant sensor fails and AssertionError when it comes to the winning state
     */
    private int getFrontDistance() throws Exception{
        try {
            //if forward sensor works, use it to measure forward distance
            if (robot.hasOperationalSensor(Direction.FORWARD)) {
                frontDistance = robot.distanceToObstacle(Direction.FORWARD);
                //if forward sensor doesn't work, try left sensor
            }else if (robot.hasOperationalSensor(Direction.LEFT)) {
                robot.rotate(Turn.RIGHT);
                frontDistance = robot.distanceToObstacle(Direction.LEFT);
                robot.rotate(Turn.LEFT);
                //if left and forward sensor don't work,try right sensor
            }else if (robot.hasOperationalSensor(Direction.RIGHT)) {
                robot.rotate(Turn.LEFT);
                frontDistance = robot.distanceToObstacle(Direction.RIGHT);
                robot.rotate(Turn.RIGHT);
                //if all other sensors don't work, try back sensor
            }else if (robot.hasOperationalSensor(Direction.BACKWARD)) {
                robot.rotate(Turn.AROUND);
                frontDistance = robot.distanceToObstacle(Direction.BACKWARD);
                robot.rotate(Turn.AROUND);
            }
        }catch(Exception e) {
            throw new Exception();
        }catch(AssertionError a) {
            throw new AssertionError();
        }
        return frontDistance;
    }

    /**
     * Drive the robot to the exit following the left wall
     * @return if the robot has stopped, return true; otherwise false.
     */
    private Boolean driveFollowLeftWall() {
        try {
            //follow the left wall
            if (leftDistance==0) {
                //if a wall is in the front, turn right;
                //if not, keep moving forward
                if (frontDistance==0) {
                    robot.rotate(Turn.RIGHT);
                    //if there is wall in the front, turn right
                    //if not, move one step forward
                    try {
                        if (this.getFrontDistance()==0) {
                            robot.rotate(Turn.RIGHT);
                        }else {
                            robot.move(1, false);}
                    }catch(Exception e) {return false;}
                }else {robot.move(1, false);}
                //if no wall on the left
            }else {
                robot.rotate(Turn.LEFT);
                robot.move(1, false);
            }
            return false;
        }catch (AssertionError e){
            //robot has stopped (eg.run out of energy)
            return true;
        }
    }

    /**
     * Returns the total energy consumption of the journey, i.e.,
     * the difference between the robot's initial energy level at
     * the starting position and its energy level at the exit position.
     * This is used as a measure of efficiency for a robot driver.
     */
    @Override
    public float getEnergyConsumption() {
        //to-do
        return 3000-robot.getBatteryLevel();
    }

    /**
     * Returns the total length of the journey in number of cells traversed.
     * Being at the initial position counts as 0.
     * This is used as a measure of efficiency for a robot driver.
     */
    @Override
    public int getPathLength() {
        return robot.getOdometerReading();
    }

    @Override
    public void stopRunning() {
        stop = true;
    }

    @Override
    public void restartRunning() {
        stop = false;
    }

    /**
     * run the the driver thread
     */
    @Override
    public void run() {
        try {
            this.drive2Exit();
        } catch(Exception e) {
            //
        }
    }

}


