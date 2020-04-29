package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WinningActivity extends AppCompatActivity{

    private int odoMeter;
    private int energyConsump;
    private int short_path;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));

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
     * onClick method for tap in winning screen
     * @param view
     */
    public void returnToStart(View view){
        Log.v("Restart","Switch to initial state");
        Toast.makeText(this,"Start Screen",Toast.LENGTH_SHORT/2).show();
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

