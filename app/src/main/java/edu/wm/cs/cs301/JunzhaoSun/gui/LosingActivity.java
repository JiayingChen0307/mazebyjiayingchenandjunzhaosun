package edu.wm.cs.cs301.JunzhaoSun.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LosingActivity extends AppCompatActivity {

    private int odoMeter;
    private int energyConsump;
    private int short_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);

        TextView energy = findViewById(R.id.energy);
        TextView userpath = findViewById(R.id.userpath);
        TextView shortpath = findViewById(R.id.shortpath);

        odoMeter = getIntent().getIntExtra(Constants.Odo_KEY,0);
        energyConsump = getIntent().getIntExtra(Constants.Energy_KEY, 0);
        short_path = getIntent().getIntExtra(Constants.Path_KEY, 0);

        String energyText = "Your energy consumption is " + energyConsump;
        String userText = "Your path length is " + odoMeter;
        String shortText = "The shortest path is " + short_path;

        energy.setText(energyText);
        userpath.setText(userText);
        shortpath.setText(shortText);
    }

    /**
     * onClick method for tap in losing screen
     * @param view
     */
    public void returnToStart(View view){
        Log.v("Restart","Switch to initial state");
        Toast.makeText(this,"Start Screen",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        finish();
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
}