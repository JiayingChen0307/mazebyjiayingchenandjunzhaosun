package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class PlayAnimationActivity extends AppCompatActivity {

    private ToggleButton toggle4Maze;
    private ToggleButton toggle4solution;
    private ToggleButton toggle4wall;
    private Button toggle4stop;

    private RobotDriver driver;
    BasicRobot robot;
    private StatePlaying sPlaying;
    private MazePanel panel;
    private int shortpath;
    private int odoMeter;
    private int energyConsump;

    private Thread thread;

    //sensor threads
    private Thread leftThread;
    private Thread rightThread;
    private Thread forwardThread;
    private Thread backwardThread;

    private Handler handler;


    /**
     * initialize each toggle button in playAnimation screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        toggle4Maze=(ToggleButton)findViewById(R.id.toggle4maze);
        toggle4solution=(ToggleButton)findViewById(R.id.toggle4solution);
        toggle4wall=(ToggleButton)findViewById(R.id.toggle4wall);
        toggle4stop=(Button)findViewById(R.id.button3);



        panel=findViewById(R.id.mazePanel);

        String driverType=getIntent().getStringExtra(AMazeActivity.DRIVER_KEY);
        if(driverType.equals("WallFollower")){
            driver = new WallFollower();
        }else{
            driver = new Wizard();
        }

        robot = new BasicRobot();
        sPlaying = new StatePlaying();
        sPlaying.setMazeConfiguration(GeneratingActivity.maze);
        sPlaying.setActivity(this);
        robot.setMaze(sPlaying);
        try {
            shortpath = GeneratingActivity.maze.getDistanceToExit(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
        }catch(Exception e){
            //do nothing
        }

        handler = new Handler();
        TextView remainEnergy = findViewById(R.id.remainEnergy);
        ProgressBar remainBar = findViewById(R.id.remainBar);
        remainBar.setMax((int)robot.getBatteryLevel());
        sPlaying.setTextBar(remainEnergy, remainBar, handler);

        sPlaying.start(panel);

        driver.setDimensions(GeneratingActivity.maze.getWidth(), GeneratingActivity.maze.getHeight());
        driver.setRobot(robot);
        driver.setDistance(GeneratingActivity.maze.getMazedists());

        thread = null;

    }

    /**
     * onClick method for Go2Winning
     */
    public void switchToWinning() {

        shutDownAllThread();

        odoMeter = robot.getOdometerReading();
        energyConsump = 3000 - (int)robot.getBatteryLevel();
        Log.v("End", "Switch to state Winning");

        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra(Constants.Odo_KEY, odoMeter);
        intent.putExtra(Constants.Energy_KEY, energyConsump);
        intent.putExtra(Constants.Path_KEY, shortpath);
        startActivity(intent);
        finish();
    }
    /**
     * onClick method for Go2Losing
     */
    public void switchToLosing() {

        shutDownAllThread();

        odoMeter = robot.getOdometerReading();
        energyConsump = 3000 - (int)robot.getBatteryLevel();
        Log.v("End", "Switch to state Losing");

        Intent intent = new Intent(this, LosingActivity.class);

        intent.putExtra(Constants.Odo_KEY, odoMeter);
        intent.putExtra(Constants.Energy_KEY, energyConsump);
        intent.putExtra(Constants.Path_KEY, shortpath);

        startActivity(intent);
        finish();
    }

    /**
     * onClick method for togglebutton maze
     * @param view
     */
    public void MazeOn(View view) {
        Log.v("Togglemaze", "Toggle maze");
        Toast.makeText(this, "Toggle maze", Toast.LENGTH_SHORT/20).show();
        sPlaying.keyDown(Constants.UserInput.ToggleFullMap,0);

}
    /**
     * onClick method for togglebutton solution
     * @param view
     */
    public void SolutionOn(View view) {
        Log.v("Togglesolution", "Toggle solution");
        Toast.makeText(this, "Toggle solution", Toast.LENGTH_SHORT/20).show();
        sPlaying.keyDown(Constants.UserInput.ToggleSolution,0);

    }
    /**
     * onClick method for togglebutton wall
     * @param view
     */
    public void WallOn(View view) {
        Log.v("ToggleWall", "wall button is toggled");
        Toast.makeText(this, "Toggle wall", Toast.LENGTH_SHORT/20).show();
        sPlaying.keyDown(Constants.UserInput.ToggleLocalMap,0);
    }
    /**
     * onClick method for stop button
     * @param view
     */
    public void Stop(View view) {
        if (thread != null) {
            driver.stopRunning();
            thread = null;
            toggle4stop.setText(R.string.restart);
            Log.v("Stop", "Driver stops");
        } else {
            driver.restartRunning();
            thread = new Thread(driver);
            thread.start();
            toggle4stop.setText(R.string.stop);
            Log.v("Restart", "Driver restarts");
        }
    }

    /**
     * get back to title screen
     */
    @Override
    public void onBackPressed() {
        shutDownAllThread();

        Log.v("BackToWelcome","Back to welcome page");
        Toast.makeText(this,"Back to welcome page", Toast.LENGTH_SHORT/10).show();
        super.onBackPressed();
        finish();
    }

    /**
     * onClick method for zoomin button
     * @param view
     */
    public void zoomIn(View view) {
        Log.v("zoomIn", "Zoom in");
        Toast.makeText(this, "Zoom in", Toast.LENGTH_SHORT/20).show();
        sPlaying.keyDown(Constants.UserInput.ZoomIn,0);
    }

    /**
     * onClick method for zoomOut button
     * @param view
     */
    public void zoomOut(View view) {
        Log.v("zoomOut", "Zoom out");
        Toast.makeText(this, "Zoom out", Toast.LENGTH_SHORT/20).show();
        sPlaying.keyDown(Constants.UserInput.ZoomOut,0);
    }

    public void ForFail(View view){
        Log.v("ToggleForwardSensor", "Toggle forward sensor");
        Toast.makeText(this, "ToggleForwardSensor", Toast.LENGTH_SHORT/20).show();
        if (forwardThread == null){
            forwardThread = new Thread(new RobotSensorOperator(robot, Robot.Direction.FORWARD));
            forwardThread.start();
        } else{
            forwardThread.interrupt();
            forwardThread = null;
        }
    }

    public void LeftFail(View view){
        Log.v("ToggleLeftSensor", "Toggle left sensor");
        Toast.makeText(this, "ToggleLeftSensor", Toast.LENGTH_SHORT/20).show();
        if (leftThread == null){
            leftThread = new Thread(new RobotSensorOperator(robot, Robot.Direction.FORWARD));
            leftThread.start();
        } else{
            leftThread.interrupt();
            leftThread = null;
        }
    }

    public void RightFail(View view){
        Log.v("ToggleRightSensor", "Toggle right sensor");
        Toast.makeText(this, "Toggle right sensor", Toast.LENGTH_SHORT/20).show();
        if (rightThread == null){
            rightThread = new Thread(new RobotSensorOperator(robot, Robot.Direction.FORWARD));
            rightThread.start();
        } else{
            rightThread.interrupt();
            rightThread = null;
        }
    }

    public void BackFail(View view){
        Log.v("ToggleBackSensor", "Toggle backward sensor");
        Toast.makeText(this, "ToggleBackSensor", Toast.LENGTH_SHORT/20).show();
        if (backwardThread == null){
            backwardThread = new Thread(new RobotSensorOperator(robot, Robot.Direction.FORWARD));
            backwardThread.start();
        } else{
            backwardThread.interrupt();
            backwardThread = null;
        }

    }

    /**
     * stop all running thread, used for out of activity handling
     */
    private void shutDownAllThread(){
        driver.stopRunning();
        thread = null;
        if (leftThread != null){
            leftThread.interrupt();
            leftThread = null;
        }
        if (rightThread != null){
            rightThread.interrupt();
            rightThread = null;
        }
        if (forwardThread != null){
            forwardThread.interrupt();
            forwardThread = null;
        }
        if (backwardThread != null){
            backwardThread.interrupt();
            backwardThread = null;
        }
    }

}
