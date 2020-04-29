package edu.wm.cs.cs301.JunzhaoSun.gui;


import android.util.Log;

import edu.wm.cs.cs301.JunzhaoSun.generation.*;
import edu.wm.cs.cs301.JunzhaoSun.gui.Robot.*;

/**
 * Wizard driver uses methods from BasicRobot and Controller to move/jump to the exit.
 * By type casting robot as BasicRobot, I get the public field controller from BasicRobot
 * and use it to getMazeConfiguration, retrieving implemented methods.
 * @author Junzhao Sun
 *
 */
public class Wizard implements RobotDriver{

    private BasicRobot robot;
    private Distance myDistance;
    private Maze maze;
    private int mazeWidth;
    private int mazeHeight;
    private boolean stop = false;

    //constructor
    public Wizard() {
        robot=null;
        myDistance=null;
        mazeWidth=0;
        mazeHeight=0;
    }
    /**
     * typecasting robot as BasicRobot; also set maze field
     */
    @Override
    public void setRobot(Robot r) {
        robot=(BasicRobot)r;
        maze=GeneratingActivity.maze;
    }
    /**
     * set maze dimension to the required one
     */
    @Override
    public void setDimensions(int width, int height) {
        mazeWidth=width;
        mazeHeight=height;
    }
    /**
     * set Distance to the required one
     */
    @Override
    public void setDistance(Distance distance) {

        myDistance=distance;
    }

    /**
     * wizard does not rely on sensor information,
     * so we return directly for this method.
     */
    @Override
    public void triggerUpdateSensorInformation() {
        return;
    }
    /**
     * the method let the robot automatically drive to the exit.
     * While current position is not exit, the robot continuouslly move or jump to its neighbor
     * which is closer to the exit.
     * Once it is at exit, break out of the while loop and turn the robot to the direction
     * facing the exit and move forward.
     */
    @Override
    public boolean drive2Exit() throws Exception {

        while (!stop) {
            //get current position and direction
            int curx=robot.getCurrentPosition()[0];
            int cury=robot.getCurrentPosition()[1];
            CardinalDirection curdir=robot.getCurrentDirection();
            //get position of neighbor closer to exit, which we can walk to.
            int neighborX=maze.getNeighborCloserToExit(curx, cury)[0];
            int neighborY=maze.getNeighborCloserToExit(curx, cury)[1];
            //get the distance of NeighborCloserToExit to exit.
            int walkDist=maze.getDistanceToExit(neighborX, neighborY);
            //if we has a wall in front/on the left/on the right/ on the back of us, decide whether to jump
            if(maze.hasWall(curx,cury,curdir) && decideJump(curx,cury,curdir,walkDist)){
                robot.jump();
            }else if(maze.hasWall(curx,cury,curdir.rotateClockwise()) && decideJump(curx,cury,curdir.rotateClockwise(),walkDist)){
                robot.rotate(Turn.LEFT);
                robot.jump();
            }else if(maze.hasWall(curx,cury,curdir.oppositeDirection().rotateClockwise()) && decideJump(curx,cury,curdir.rotateClockwise().oppositeDirection(),walkDist)){
                robot.rotate(Turn.RIGHT);
                robot.jump();
            }else if(maze.hasWall(curx,cury,curdir.oppositeDirection()) && decideJump(curx,cury,curdir.oppositeDirection(),walkDist)){
                robot.rotate(Turn.AROUND);
                robot.jump();
            }else {
                //if no wall around current position, we move closer to exit
                int differenceX=neighborX-curx;
                int differenceY=neighborY-cury;
                if(differenceY==0) {
                    if(differenceX==1) {
                        turn(curdir,CardinalDirection.East);
                        robot.move(1, false);
                    }else if(differenceX==-1) {
                        turn(curdir,CardinalDirection.West);
                        robot.move(1,false);
                    }
                }else if(differenceX==0) {
                    if(differenceY==1) {
                        turn(curdir,CardinalDirection.South);
                        robot.move(1, false);
                    }else if(differenceY==-1) {
                        turn(curdir,CardinalDirection.North);
                        robot.move(1,false);
                    }
                }
            }
            //if after move/jump, we reach the exit, return true;
            if(robot.isAtExit()) {
                if(robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
                    robot.move(1, false);
                }
                else if(robot.canSeeThroughTheExitIntoEternity(Direction.LEFT)) {
                    robot.rotate(Turn.LEFT);
                    robot.move(1, false);
                }else if(robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT)) {
                    robot.rotate(Turn.RIGHT);
                    robot.move(1, false);
                }else {
                    robot.rotate(Turn.AROUND);
                    robot.move(1, false);
                }
                return true;
            }

            if (robot.hasStopped()) return false;

            if (robot.hasWon()) return true;
        }

        return false;

    }

    /**
     * A private method to turn robot according to input CardinalDirections
     * @param curdir
     * @param direction
     */
    private void turn(CardinalDirection curdir,CardinalDirection direction) {
        if(direction == curdir.oppositeDirection()) {
            robot.rotate(Turn.AROUND);
        }
        if (direction == curdir.rotateClockwise()) {
            robot.rotate(Turn.LEFT);
        }
        if (direction == curdir.oppositeDirection().rotateClockwise()) {
            robot.rotate(Turn.RIGHT);
        }
    }
    /**
     * decideJump() determines whether Robot should jump if it has walls around
     * The method returns true if jump could save robot at least 5 pathlength
     * than moving toward the exit.
     * The method gets the position of the neighbor cell which is adjacent to wall
     * and gets its distance to exit.
     * Then the method compares the distance with the one of the neighbor cell
     * closer to the exit(walk only). If difference>=5, return true.
     * @param x
     * @param y
     * @param dir
     * @param walkdist
     * @return
     */
    private boolean decideJump(int x, int y, CardinalDirection dir,int walkdist) {
        int neighborWallx=x+dir.getDirection()[0];
        int neighborWally=y+dir.getDirection()[1];
        //if position invalid
        if (neighborWallx < 0 || neighborWallx >= mazeWidth
                || neighborWally < 0 || neighborWally >= mazeHeight)
            return false;
        int neighborWalldist=maze.getDistanceToExit(neighborWallx, neighborWally);
        if (walkdist-neighborWalldist>=5) {
            return true;
        }
        return false;
    }
    /**
     * get current energyConsumption
     */
    @Override
    public float getEnergyConsumption() {

        return 3000-robot.getBatteryLevel();
    }
    /**
     * get current pathLength
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
     * try to drive robot to exit and catch exception
     */
    @Override
    public void run() {
        try {
            this.drive2Exit();

        } catch (Exception e) {
            Log.e("Exception in Wizard","Exception in Wizard");
            e.printStackTrace();
        }

    }

}

