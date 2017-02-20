package com.ardel.gyroball;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity implements SensorEventListener{


    CustomDrawableView mCustomDrawableView = null;
    public float xPosition, xAcceleration, xVelocity = 0.0f;
    public float yPosition, yAcceleration, yVelocity = 0.0f;
    final int ballWidth = 175;
    final int ballHeight = 175;
    public float xmax, ymax;
    private Bitmap mBitmap;
    private SensorManager sensorManager = null;
    public float frameTime = 0.65f;
    private static final float COR = 0.4f; //coefficient of restitution

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        //Set FullScreen & portrait
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);
        // setContentView(R.layout.main);

        //Calculate Boundry
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        xmax = (float)size.x - ballWidth;
        ymax = (float)size.y - ballHeight;
        //set start position of ball on screen center
        xPosition = xmax/2;
        yPosition = ymax/2;
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Set sensor values as acceleration
            xAcceleration = -sensorEvent.values[0];
            yAcceleration =  sensorEvent.values[1];
            updateBall();
        }
    }

    private void updateBall() {

        //Calculate new speed
        xVelocity += (xAcceleration * frameTime);
        yVelocity += (yAcceleration * frameTime);

        //Calc distance travelled in that time
        xPosition += (xVelocity*frameTime) + ((xVelocity/2)*(float) Math.pow(frameTime,2));
        yPosition += (yVelocity*frameTime) + ((yVelocity/2)*(float) Math.pow(frameTime,2));

        //if hit edge of screen
        if (xPosition >= xmax) {
            xVelocity = -xVelocity * COR;
            xPosition = xmax;

        } else if (xPosition <= 0) {
            xVelocity = -xVelocity * COR;
            xPosition = 0;
        }
        if (yPosition >= ymax) {
            yVelocity = -yVelocity * COR;
            yPosition = ymax;

        } else if (yPosition <= 0) {
            yVelocity = -yVelocity * COR;
            yPosition = 0;
        }
    }


    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // TODO Auto-generated method stub
    }


    public class CustomDrawableView extends View
    {
        public CustomDrawableView(Context context)
        {
            super(context);
            Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            mBitmap = Bitmap.createScaledBitmap(ball, ballWidth, ballHeight, true);

        }

        protected void onDraw(Canvas canvas)
        {
            final Bitmap bitmap = mBitmap;
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);
            invalidate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void onStop()
    {
        sensorManager.unregisterListener(this);
        super.onStop();
    }
}
