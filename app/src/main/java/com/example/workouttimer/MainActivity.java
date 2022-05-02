package com.example.workouttimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Chronometer chronometer;
    TextView mainView;
    EditText taskTextEdit;
    SharedPreferences sp;
    long chronoTime;
    boolean running = false;
    long pauseOffset;
    public String studyTime;
    public String studyType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainView = findViewById(R.id.mainView);
        taskTextEdit = findViewById(R.id.taskTextEdit);
        chronometer = findViewById(R.id.chronometer);

        sp = getSharedPreferences("SharedPrefs", MODE_PRIVATE);

        updateStudyDetails();

        if(savedInstanceState!=null){

            chronometer.setBase(SystemClock.elapsedRealtime() - savedInstanceState.getLong("Chronotime"));


            pauseOffset = savedInstanceState.getLong("PauseOffset");
            running = savedInstanceState.getBoolean("running");
            chronoTime = SystemClock.elapsedRealtime() - chronometer.getBase();;

            if(running){
                chronometer.start();
            }
        }

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            @Override
            public void onChronometerTick(Chronometer chronometer)
            {
                chronoTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            }
        });
    }
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        //Chronometer is stopped when the activity is destroyed
        chronometer.stop();

        //Saving auxiliary variables in instance state
        savedInstanceState.putLong("Chronotime", chronoTime);
        savedInstanceState.putLong("PauseOffset", pauseOffset);
        savedInstanceState.putBoolean("running", running);
    }

    public void updateStudyDetails(){
        System.out.println("Update function is running");
        studyType = sp.getString("studyType", "");
        studyTime = sp.getString("studyTime", "");

        System.out.println(studyTime);
        System.out.println(studyType);

        if(!studyTime.equals("")) {
            System.out.println("Loop 1");
            if(!studyType.equals("")){
                System.out.println("Loop 2");
                mainView.setText("You spent " + studyTime + " on " + studyType + " last time.");
            }
            else{
               mainView.setText("You spent " + studyTime + " on " + studyType + "last time.");
            }
        }
    }


    public void startButton(View view){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseButton(View view){
    if(running){
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        running = false;
    }
}

    public void stopButton(View v) {
        chronometer.stop();

        int mins = (int)(chronoTime/1000/60);
        int secs = (int)((chronoTime/1000)-(mins*60));
        String minsStr = String.valueOf(mins);
        String secsStr = String.valueOf(secs);

        if(mins<10){
            minsStr = ("0"+minsStr);
        }
        if(secs<10){
            secsStr = ("0"+secsStr);
        }

        String timeStr = (minsStr+":"+secsStr);
        System.out.println(timeStr);

        System.out.println(chronoTime);
        if(chronoTime>0){
            SharedPreferences.Editor studyEdit = sp.edit();
            studyEdit.putString("studyTime", timeStr);
            studyEdit.putString("studyType", taskTextEdit.getText().toString());

            studyEdit.commit();
            updateStudyDetails();
        }


        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        running = false;
        pauseOffset = 0;
        chronoTime = 0;
        running = false;
    }


}