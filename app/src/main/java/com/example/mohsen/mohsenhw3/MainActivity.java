package com.example.mohsen.mohsenhw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    public static final int fade_duration = 1500;
    public static final int start_offset = 1000;
    public static final int vibrate_time = 250;
    public static final int threshold = 200;
    public static final int shake_count = 2;
    private static Random random = new Random();
    private Vibrator vibrator;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float lastX, lastY, lastZ;
    private int shakeCount = 0; //number of counts before display a new answer
    private TextView msgTV;
    private ImageView ball;
    private Animation ballAnimation; //check the animation type of the ball
    private ArrayList<String> answers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ball = findViewById(R.id.ball);
        msgTV = findViewById(R.id.msgTV);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //define accelerometer as the sensor that we wanna use
        ballAnimation = AnimationUtils.loadAnimation(this, R.anim.shake); //load the check animation type of the ball
        answers= loadAnswers();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.shake:
                showAnswer(getAnswer(),true);
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        showAnswer(getString(R.string.shake_me),false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if(isSakeEnough(event.values[0],event.values[1],event.values[2])){
                showAnswer (getAnswer(),false);

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean isSakeEnough(float x, float y, float z) {
        double force = 0d;


        force = (Math.abs(x-lastX)+Math.abs(y-lastY)+Math.abs(z-lastZ))/SensorManager.GRAVITY_EARTH;

        lastX = x;
        lastY = y;
        lastZ = z;

        if (force > ((float) threshold/100f)){
            ball.startAnimation(ballAnimation);
            shakeCount++;
            if(shakeCount>shake_count){
                shakeCount = 0;
                lastX = 0;
                lastY = 0;
                lastZ = 0;
                return true;
            }
        }

        return false;
    }



    private void showAnswer(String answer, boolean WithAnim) {
        if (WithAnim){
            ball.startAnimation(ballAnimation);
        }

        msgTV.setVisibility(View.INVISIBLE);
        msgTV.setText(answer);
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setStartOffset(start_offset);
        msgTV.setVisibility(View.VISIBLE);
        animation.setDuration(fade_duration);

        msgTV.startAnimation(animation);
        vibrator.vibrate(vibrate_time);

    }
    private String getAnswer() {      //// generate random answers
        int randomint = random.nextInt(answers.size());
        return answers.get(randomint);

    }

    private ArrayList<String> loadAnswers() {
        ArrayList<String> list = new ArrayList<>();
        String [] tab = getResources().getStringArray(R.array.answers);

        if (tab !=null && tab.length >0){
            for (String str : tab){
                list.add(str);
            }

        }
        return list;
    }

}
