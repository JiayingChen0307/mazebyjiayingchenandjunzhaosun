package edu.wm.cs.cs301.JunzhaoSun.gui;

import edu.wm.cs.cs301.JunzhaoSun.generation.CardinalDirection;
import edu.wm.cs.cs301.JunzhaoSun.generation.Wallboard;
import edu.wm.cs.cs301.JunzhaoSun.gui.Constants.UserInput;

/**
 * BasicRobot implements methods for Robot interface.
 * For BasicRobot, we store controller, batteryLevel, etc.that will be used in methods as fields.
 * We also set energy usage as static final ints for better understanding of our codes.
 * @author Junzhao Sun & Jiaying Chen
 */
public class BasicRobot implements Robot{
    //public attributes for convenient use
    private StatePlaying sPlaying;
    private float batteryLevel;
    private int odoMeter;
    private int energyConsump;

    private boolean win = false;

    //private flags & static ints because they are only used inside of this class
    protected boolean leftFlag;
    protected boolean rightFlag;
    protected boolean forwardFlag;
    protected boolean backFlag;
    private static final int energyForSensing = 1;
    private static final int energyForFullRotation = 12;
    private static final int energyForStepForward = 5;
    private static final int t = 3000;

    //constructor
    public BasicRobot() {
        batteryLevel=3000;
        odoMeter=0;
        energyConsump=0;
        leftFlag=true;
        rightFlag=true;
        forwardFlag=true;
        backFlag=true;
    }
    /**
     * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
     * @postcondition 0 <= x < width, 0 <= y < height of the maze.
     * @return array of length 2, x = array[0], y=array[1]
     * @throws Exception if position is outside of the maze
     */
    @Override
    public int[] getCurrentPosition() throws Exception {

        return sPlaying.getCurrentPosition();

    }
    /**
     * assert currentState is the playing state and then
     * provide the current direction of the robot.
     */
    @Override
    public CardinalDirection getCurrentDirection() {

        return sPlaying.getCurrentDirection();
    }
    /**
     * Provides the robot with a reference to the controller to cooperate with.
     * The robot memorizes the controller such that this method is most likely
     * called only once and for initialization purposes.
     */
    public void setMaze(StatePlaying sPlaying) {
        this.sPlaying = sPlaying;
    }
    /**
     * return the current battery level
     */
    @Override
    public float getBatteryLevel() {
        return batteryLevel;
    }
    /**
     * set the current battery level to the input level
     */
    @Override
    public void setBatteryLevel(float level) {

        batteryLevel = level;
    }
    /**
     * get the current odometer
     */
    @Override
    public int getOdometerReading() {
        return odoMeter;
    }
    /**
     * reset odometer to 0
     */
    @Override
    public void resetOdometer() {
        odoMeter = 0;
    }
    /**
     * return energy consumption for full rotation
     */
    @Override
    public float getEnergyForFullRotation() {
        return energyForFullRotation;
    }
    /**
     * return energy consumption for stepping forward 1 step
     */
    @Override
    public float getEnergyForStepForward() {
        return energyForStepForward;
    }

    /**
     * The method detects whether the current position is at exit.
     * If so, distance to exit from current position should equal to 1.
     * @author Junzhao Sun
     */
    @Override
    public boolean isAtExit() {

        if (hasWon()) return false;
        if (hasStopped()) return false;

        int exitX=sPlaying.getCurrentPosition()[0];
        int exitY=sPlaying.getCurrentPosition()[1];
        return sPlaying.getMazeConfiguration().getDistanceToExit(exitX, exitY) == 1;
    }
    /**
     * canSeeThroughTheExitIntoEternity() determines whether current position's
     * distance to obstacle is infinity. If it is, we are at the exit.
     * In order to avoid UnsupportedOperationException thrown by distanceToObstacle(),
     * we copy basically the same codes from distanceToObstacle() so that distanceToObstacle could be computed.
     * @author Junzhao Sun
     */
    @Override
    public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {

        //if(!this.hasOperationalSensor(direction)) throw new UnsupportedOperationException("disfunctional sensor");
        if (hasWon()) return false;
        if (hasStopped()) return false;

        batteryLevel-=energyForSensing;
        energyConsump = getEnergyConsump() + energyForSensing;
        int curX=sPlaying.getCurrentPosition()[0];
        int curY=sPlaying.getCurrentPosition()[1];
        int dircount = 0;
        CardinalDirection curDirect=changeDirection(direction);
        dircount = countDistance(curX, curY, curDirect);
        //press the key to check if the robot has stopped by using the distance sensor
        sPlaying.keyDown(UserInput.Start, 0);
        //if distance = infinity, we are at the exit, so return true;
        return dircount == Integer.MAX_VALUE;
    }
    /**
     * The method employs isInRoom() from floorplan class to check whether current position is inside of a room.
     */
    @Override
    public boolean isInsideRoom() throws UnsupportedOperationException {
        int roomX=sPlaying.getCurrentPosition()[0];
        int roomY=sPlaying.getCurrentPosition()[1];
        return sPlaying.getMazeConfiguration().getFloorplan().isInRoom(roomX, roomY);
    }
    /**
     * For project 3, robot always has a room sensor.
     */
    @Override
    public boolean hasRoomSensor() {
        return true;//in this version, robot always roomsensor
    }

    /**
     * The method checks whether the robot is stopped.
     * It returns true if batteryLevel is less than or equal to 0.
     * Otherwise, returns false.
     */
    @Override
    public boolean hasStopped() {
        return (batteryLevel <= 0);
    }
    /**
     * distanceToObstacle returns the distance to a wall at a particular direction.
     * The input direction is implemented for robot, so we need to change the input direction
     * to corresponding cardinal direction first. Then we call countDistance to compute the distance.
     * @author Junzhao Sun
     */
    @Override
    public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {

        if (hasWon()) return 0;
        if (hasStopped()) return 0;

        //check sensor first, which consumes energy
        if(!this.hasOperationalSensor(direction)) throw new UnsupportedOperationException("disfunctional sensor");
        //if sensor operates, update fields
        batteryLevel-=energyForSensing;
        energyConsump = getEnergyConsump() + energyForSensing;

        int curX=sPlaying.getCurrentPosition()[0];
        int curY=sPlaying.getCurrentPosition()[1];
        int dircount = 0;
        //transform direction to cardinal direction
        CardinalDirection curDirect=changeDirection(direction);
        //return distance
        dircount = countDistance(curX, curY, curDirect);
        //press the key to check if the robot has stopped by using the distance sensor
        sPlaying.keyDown(UserInput.Start, 0);
        return dircount;
    }

    /**
     * The private method changes direction to cardinal direction.
     * @param direction
     * @return
     */
    private CardinalDirection changeDirection(Direction direction) {
        if(direction == Direction.LEFT) {
            return sPlaying.getCurrentDirection().rotateClockwise();
        } else if(direction == Direction.BACKWARD) {
            return sPlaying.getCurrentDirection().oppositeDirection();
        } else if (direction == Direction.RIGHT) {
            //turn to back and then turn left clockwise = turn right
            return sPlaying.getCurrentDirection().oppositeDirection().rotateClockwise();
        }
        return sPlaying.getCurrentDirection();
    }

    /**
     * countDistance is a private method serving for distanceToObstacle.
     * It counts the distance to obstacle in a particular direction at the current position.
     * The passing parameters are x-coordinate, y-coordinate of the current position,
     * and cardinal direction corresponding to direction of the robot.
     * Inside the method, we use getDirection() to get the list representing each cardinal direction.
     * For example, cardinal direction east corresponds to [1,0], which means the x-coordinate of
     * the current position will move to the left one step.
     * Thus we can use this to manipulate current x-coordinate and y-coordinate.
     * We use a while loop to see how many steps we need to get to the closest wall in the particular direction.
     * Finally, we return the number of steps.
     * @param startx
     * @param starty
     * @param carDirect
     * @return
     * @author Junzhao Sun
     */
    private int countDistance(int startx, int starty, CardinalDirection carDirect) {
        int[] dir = carDirect.getDirection();
        int dx=dir[0];
        int dy=dir[1];
        int count=0;
        while (true) {
            //if we are facing the exit, return infinity,
            if (!sPlaying.getMazeConfiguration().isValidPosition(startx, starty)) {
                return Integer.MAX_VALUE;
            }
            //break out of the loop if there is a wall in the input direction and we are not facing the exit
            if (sPlaying.getMazeConfiguration().getFloorplan().hasWall(startx, starty, carDirect)) {
                break;
            }
            //increase x,y coordinates
            startx += dx;
            starty += dy;
            ++count;
        }
        return count;
    }
    /**
     * The method tells if the robot has an operational distance sensor for the given direction.
     * Flags corresponding to different directions are stored as fields.
     */
    @Override
    public boolean hasOperationalSensor(Direction direction) {
        if(direction==Direction.BACKWARD) {
            return backFlag;
        }else if(direction==Direction.FORWARD) {
            return forwardFlag;
        }else if(direction == Direction.LEFT) {
            return leftFlag;
        }else {
            return rightFlag;
        }
    }
    /**
     * The method set the flag corresponding to the input direction as false to trigger sensor failure.
     */
    @Override
    public void triggerSensorFailure(Direction direction) {
        if(direction==Direction.BACKWARD) {
            backFlag=false;
        }else if(direction==Direction.FORWARD) {
            forwardFlag=false;
        }else if(direction == Direction.LEFT) {
            leftFlag=false;
        }else {
            rightFlag=false;
        }
    }
    /**
     * The method set the flag corresponding to the input direction as false
     * and always return true to notify that the robot has a sensor in the input direction.
     */
    @Override
    public boolean repairFailedSensor(Direction direction) {
        if(direction==Direction.BACKWARD) {
            backFlag=true;
        }else if(direction==Direction.FORWARD) {
            forwardFlag=true;
        }else if(direction == Direction.LEFT) {
            leftFlag=true;
        }else if (direction == Direction.RIGHT) {
            rightFlag=true;
        }
        return true;
    }

    /**
     * Turn robot on the spot for amount of degrees.
     * If robot runs out of energy, it stops, which can be checked by hasStopped() == true.
     * For example, turn around could be achieved by turning left twice, which uses keyDown.left twice.
     * @author Junzhao Sun
     */
    @Override
    public void rotate(Turn turn) {

        if (hasWon()) return;
        if (hasStopped()) return;

        if (!hasStopped()) {
            if(turn==Turn.LEFT) {
                batteryLevel-=getEnergyForFullRotation()/4;
                energyConsump = (int) (getEnergyConsump() + getEnergyForFullRotation()/4);
                sPlaying.keyDown(Constants.UserInput.Left, 0);
            } else if(turn == Turn.RIGHT) {
                batteryLevel-=getEnergyForFullRotation()/4;
                energyConsump = (int) (getEnergyConsump() + getEnergyForFullRotation()/4);
                sPlaying.keyDown(Constants.UserInput.Right, 0);
            } else if(turn == Turn.AROUND) {
                batteryLevel-=getEnergyForFullRotation()/2;
                energyConsump = (int) (getEnergyConsump() + getEnergyForFullRotation()/2);
                //turn left twice to turn around
                for(int i =  0; i < 2; ++i) {
                    sPlaying.keyDown(Constants.UserInput.Left, 0);
                }
            }
        }
    }

    /**
     * The method moves robot forward a given number of steps. A step matches a single cell.
     * If the robot runs out of energy somewhere on its way, it stops, which can be checked by hasStopped() == true.
     * If the robot hits an obstacle like a wall, its energy will decrease while odoMeter remains the same.
     * The robot cannot jump over a wall in the move method.
     * @author Junzhao Sun
     */
    @Override
    public void move(int distance, boolean manual) {

        if (hasWon()) return;
        if (hasStopped()) return;

        int move=0;
        //get current position and direction
        int curX=sPlaying.getCurrentPosition()[0];
        int curY=sPlaying.getCurrentPosition()[1];
        CardinalDirection curDirect=sPlaying.getCurrentDirection();
        int distanceToObstacle = countDistance(curX,curY,curDirect);
        //if we haven't moved the input distance and the robot hasn't run out of energy.
        while ( move < distance) {
            if (hasWon()) return;
            if (hasStopped()) return;
            //first decreases energy
            batteryLevel-=energyForStepForward;
            energyConsump = getEnergyConsump() + energyForStepForward;
            if( move < distanceToObstacle) {
                //if we haven't met a wall, increase odometer by 1
                odoMeter+=1;
            }
            sPlaying.keyDown(UserInput.Up, 0);
            move+=1;
        }
    }
    /**
     * Makes robot move in a forward direction even if there is a wall in front of it.
     * In this sense, the robot jumps over the wall if necessary.
     * The distance is always 1 step and the direction is always forward.
     * We need to make sure that odometer only increases when robot won't jump out of the maze.
     * @author Junzhao Sun
     */
    @Override
    public void jump() throws Exception {

        if (hasWon()) return;
        if (hasStopped()) return;

        energyConsump = getEnergyConsump() + 50;
        batteryLevel-=50;
        //get current position and direction
        int curX=sPlaying.getCurrentPosition()[0];
        int curY=sPlaying.getCurrentPosition()[1];
        CardinalDirection curDirect = sPlaying.getCurrentDirection();
        //use wallboard.getNeighbor() to check whether the cell in front of current position is inside the maze
        Wallboard curWallboard = new Wallboard(curX, curY, curDirect);
        int neighborX=curWallboard.getNeighborX();
        int neighborY=curWallboard.getNeighborY();
        if(sPlaying.getMazeConfiguration().isValidPosition(neighborX, neighborY)) {
            //if after jump, we will still be inside of the maze, odoMeter increases by 1
            odoMeter+=1;
            sPlaying.keyDown(UserInput.Jump, 0);
        }
    }

    @Override
    public boolean hasWon() {
        return win;
    }

    @Override
    public void setWin() {
        win = true;
    }

    /**
     * Set the energy consumption of this robot
     * @param energy
     */
    public void setEnergyConsump(int energy) {
        this.energyConsump=energy;
    }

    /**
     * Obtain the energy consumption of this robot
     * @return
     */
    public int getEnergyConsump() {
        return this.energyConsump;
    }

}

