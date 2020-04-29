package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import edu.wm.cs.cs301.JunzhaoSun.generation.Factory;
import edu.wm.cs.cs301.JunzhaoSun.generation.Maze;
import edu.wm.cs.cs301.JunzhaoSun.generation.MazeFactory;
import edu.wm.cs.cs301.JunzhaoSun.generation.Order;

public class GeneratingActivity extends AppCompatActivity implements Order{
    /**
     * fields for progressBar, thread and driver
     */
    private String driver;

    private Order.Builder mazeBuilder;

    private int skillLevel;

    private ProgressBar progressBar;

    private Handler handler;

    private Factory mazeFactory;

    private Boolean revisit;

    private int builderIndex;

    public static Maze maze;
    /**
     * Initialize progressBar and let it run using a for loop.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        driver = getIntent().getStringExtra(AMazeActivity.DRIVER_KEY);
        skillLevel = getIntent().getIntExtra(AMazeActivity.SkillLevel, 0);
        revisit = getIntent().getBooleanExtra(AMazeActivity.REV_KEY,false);
        String mazetype = getIntent().getStringExtra(AMazeActivity.Builder_KEY);
        if (mazetype.equals ("DFS")){
            mazeBuilder=Builder.DFS;
            builderIndex = 0;
        }else if (mazetype.equals("Prim")){
            mazeBuilder=Builder.Prim;
            builderIndex = 1;
        }else{
            mazeBuilder=Builder.Eller;
            builderIndex = 2;
        }

        progressBar= findViewById(R.id.progressBar);

        handler = new Handler();

        //start generating
        mazeFactory = new MazeFactory();
        mazeFactory.order(this);
    }

    /**
     * Get to different play activity according to Driver
     */
    public void startPlaying(){
        Log.v("Start","Switch to state Playing");

        if (driver.equals("Manual")){
            startActivity(new Intent(this, PlayManuallyActivity.class));
        }else{
            Intent intent = new Intent(this, PlayAnimationActivity.class);
            intent.putExtra(AMazeActivity.DRIVER_KEY, driver);
            startActivity(intent);
        }

        finish();
    }

    /**
     * override onBackPressed so the user could get back to title screen by clicking back button
     */
    @Override
    public void onBackPressed() {
        Log.v("BackToWelcome","Back to welcome page");
        super.onBackPressed();
        mazeFactory.cancel();
        finish();
    }


    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public Builder getBuilder() {
        return mazeBuilder;
    }

    @Override
    public boolean isPerfect() {
        return false;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        maze=mazeConfig;
        startPlaying();
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public int getSeed(){
        SharedPreferences mazePreference = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mazePreference.edit();
        String key = String.valueOf(builderIndex) + String.valueOf(skillLevel);
        int seed;
        if (!revisit || !mazePreference.contains(key)){
            seed = (int)(Math.random() * 100);
            myEditor.putInt(key, seed);
            myEditor.apply();
        } else{
            seed = mazePreference.getInt(key, 1);
        }

        return seed;

    }
}
