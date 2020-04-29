package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.JunzhaoSun.gui.Constants.UserInput;
import edu.wm.cs.cs301.JunzhaoSun.gui.Robot.Turn;

public class PlayManuallyActivity extends AppCompatActivity {

    private ToggleButton toggle4Maze;
    private ToggleButton toggle4solution;
    private ToggleButton toggle4wall;
    protected BasicRobot robot;
    private MazePanel panel;
    public static StatePlaying sPlaying;
    private int odoMeter;
    private int energyConsump;
    private int shortpath;


    /**
     * initialize each toggle button in playManual screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        toggle4Maze=findViewById(R.id.toggle4maze);
        toggle4solution=findViewById(R.id.toggle4solution);
        toggle4wall=findViewById(R.id.toggle4wall);

        panel=findViewById(R.id.panel);

        robot = new BasicRobot();
        sPlaying = new StatePlaying();
        sPlaying.setMazeConfiguration(GeneratingActivity.maze);
        sPlaying.setActivity(this);
        robot.setMaze(sPlaying);
        sPlaying.start(panel);
        try {
            shortpath = GeneratingActivity.maze.getDistanceToExit(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
        }catch(Exception e){
            //do nothing
        }
    }
    /**
     * onClick method for Go2Winning
     */
    public void switchToWinning() {
        odoMeter = robot.getOdometerReading();
        energyConsump = 3000 - (int)robot.getBatteryLevel();
        Log.v("End", "Switch to state Winning");
        Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();

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
        odoMeter = robot.getOdometerReading();
        energyConsump = 3000 - (int)robot.getBatteryLevel();
        Log.v("End", "Switch to state Losing");
        Toast.makeText(this, "You lose!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LosingActivity.class);

        intent.putExtra(Constants.Odo_KEY, odoMeter);
        intent.putExtra(Constants.Energy_KEY, energyConsump);
        intent.putExtra(Constants.Path_KEY, shortpath);

        startActivity(intent);
        finish();
    }

    /**
     * OnClick method for left button
     * @param view
     */
    public void turnLeft(View view){
        robot.rotate(Turn.LEFT);
        Log.v("turnLeft", "Turn left");
        Toast.makeText(this, "Turn left", Toast.LENGTH_SHORT).show();
    }

    /**
     * OnClick method for upward button
     * @param view
     */
    public void moveForward(View view) {
        robot.move(1,true);
        Log.v("moveForward", "Move Forward");
        Toast.makeText(this, "Move Forward", Toast.LENGTH_SHORT).show();

    }

    /**
     * OnClick method for right button
     * @param view
     */
    public void turnRight(View view) {
        robot.rotate(Turn.RIGHT);
        Log.v("turnRight", "Turn right");
        Toast.makeText(this, "Turn right", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Toggle wall", Toast.LENGTH_SHORT/2).show();
        sPlaying.keyDown(UserInput.ToggleLocalMap,0);
    }
    /**
     * get back to title screen
     */
    @Override
    public void onBackPressed() {
        Log.v("BackToWelcome","Back to welcome page");
        super.onBackPressed();
        finish();
    }
    /**
     * onClick method for zoomOut button
     * @param view
     */
    public void zoomOut(View view) {
        sPlaying.keyDown(UserInput.ZoomOut,0);
        Log.v("zoomOut", "Zoom out");
        Toast.makeText(this, "Zoom out", Toast.LENGTH_SHORT/2).show();
    }
    /**
     * onClick method for zoomIn button
     * @param view
     */
    public void zoomIn(View view) {
        sPlaying.keyDown(UserInput.ZoomIn,0);
        Log.v("zoomIn", "Zoom in");
        Toast.makeText(this, "Zoom in", Toast.LENGTH_SHORT/2).show();
    }


}