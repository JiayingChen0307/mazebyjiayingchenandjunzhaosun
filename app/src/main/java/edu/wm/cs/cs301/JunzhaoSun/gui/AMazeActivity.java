package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AMazeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /**
     * initialize three fields to store input keys for mazeDriver, mazeBuilder and skillLevel.
     */
    public static final String DRIVER_KEY = "mazeDriver";
    public static final String Builder_KEY= "mazeBuilder";
    public static final String SkillLevel= "skillLevel";
    public static final String REV_KEY= "revisit";

    private String mazeBuilder = "DFS";
    private String mazeDriver="Manual";
    private int skillLevel=0;

    private SeekBar skillBar;

    /**
     * Set up spinner and seekBar here
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);

        Spinner Alg_spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Algorithms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Alg_spinner.setAdapter(adapter);
        Alg_spinner.setOnItemSelectedListener(this);

        Spinner Driver_spinner = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter_ = ArrayAdapter.createFromResource(this,
                R.array.Drivers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Driver_spinner.setAdapter(adapter_);
        Driver_spinner.setOnItemSelectedListener(this);

        skillBar=(SeekBar)findViewById(R.id.seekBar);
    }

    /**
     * Get which driver/algorithm is selected by the user and send toast
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text=parent.getItemAtPosition(position).toString();

        String selected =(String) parent.getItemAtPosition(position);

        switch (selected){
            case "DFS":
                mazeBuilder="DFS";
                Log.v("builderSelection","mazeBuilder:DFS");
                Toast.makeText(this,"mazeBuilder:DFS",Toast.LENGTH_SHORT/10).show();
                break;
            case "Prim":
                mazeBuilder="Prim";
                Log.v("builderSelection","mazeBuilder:Prim");
                Toast.makeText(this,"mazeBuilder:Prim",Toast.LENGTH_SHORT/10).show();
                break;
            case "Eller":
                mazeBuilder="Eller";
                Log.v("builderSelection","mazeBuilder:Eller");
                Toast.makeText(this,"mazeBuilder:Eller",Toast.LENGTH_SHORT/10).show();
                break;
            case "Manual":
                mazeDriver="Manual";
                Log.v("driverSelection","mazeDriver:Manual");
                Toast.makeText(this,"mazeDriver:Manual",Toast.LENGTH_SHORT/10).show();
                break;
            case "Wizard":
                mazeDriver="Wizard";
                Log.v("driverSelection","mazeDriver:Wizard");
                Toast.makeText(this,"mazeDriver:Wizard",Toast.LENGTH_SHORT/10).show();
                break;
            case "WallFollower":
                mazeDriver="WallFollower";
                Log.v("driverSelection","mazeDriver:WallFollower");
                Toast.makeText(this,"mazeDriver:WallFollower",Toast.LENGTH_SHORT/10).show();
                break;
            default:
                break;
        }
    }

    /**
     * an empty override method to cope with OnItemSelected
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * OnClick method for Start Button. It passes driver, skillLevel, builder to an intent.
     * @param view
     */
    public void startGenerating(View view){
        Log.v("Start","Switch to state Generating");
        Toast.makeText(this,"Maze Generated",Toast.LENGTH_SHORT/10).show();

        skillLevel=skillBar.getProgress();

        Intent intent= new Intent(this, GeneratingActivity.class);
        intent.putExtra(DRIVER_KEY, mazeDriver);
        intent.putExtra(SkillLevel,skillLevel);
        intent.putExtra(Builder_KEY,mazeBuilder);

        startActivity(intent);
    }

    /**
     * OnClick method for Revisit Button. It passes driver, skillLevel, builder to an intent.
     * @param view
     */
    public void revisit(View view){
        Log.v("Revisit","Back to previous Maze");
        Toast.makeText(this,"Previous Playing screen", Toast.LENGTH_SHORT/10).show();

        skillLevel=skillBar.getProgress();

        Intent intent= new Intent(this, GeneratingActivity.class);
        intent.putExtra(DRIVER_KEY, mazeDriver);
        intent.putExtra(SkillLevel,skillLevel);
        intent.putExtra(Builder_KEY,mazeBuilder);
        intent.putExtra(REV_KEY, true);

        startActivity(intent);
    }

}
