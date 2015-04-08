package kcorkrad.example.foodtimer.foodtimer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Chronometer;

public class FoodTimer extends Activity implements View.OnClickListener {

    Timer timer;
    TimerTask timerTask;
    private boolean timerNowRunning = false;
    private Chronometer chronometer;
    private TextView countDownText;
    private TextView biteNumberText;
    private TextView timeElapsedHeader;
    private TextView timingIntervalAnnounce;
    private int biteNumber = 0;
    private Button startStopSession;
    private Button yummyButton;
    private YummyCountDownTimer countDownTimer;
    // private long timeElapsed;

    private long intervalTimeSeconds = 15;
    // Vibrator buzz = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    // we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        startStopSession = (Button) findViewById(R.id.startStopSession);
        yummyButton = (Button) findViewById(R.id.yummy);
        countDownText = (TextView) findViewById(R.id.timeElapsed);
        biteNumberText = (TextView) findViewById(R.id.biteNumber);
        countDownTimer = new YummyCountDownTimer(intervalTimeSeconds * 1000, 1000);
        timeElapsedHeader = (TextView) findViewById(R.id.timeElapsedHeader);
        timeElapsedHeader.setText(getString(R.string.startSession));
        timingIntervalAnnounce = (TextView) findViewById(R.id.timingIntervalAnnounce);
        timingIntervalAnnounce.setText("Timing " + intervalTimeSeconds + " second bites");

        (findViewById(R.id.startStopSession)).setOnClickListener(this);
        (findViewById(R.id.yummy)).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        //* EDR edit: leave this startTimerTask to user to click start
        //startTimerTask();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startStopSession:
                if (!timerNowRunning){
                    timeElapsedHeader.setText("Eating Time! Chew and swallow for");
                    yummyButton.setText(getString(R.string.biteAccepted));
                    incrementBite();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    startTimerTask();
                    chronometer.start();
                    countDownTimer.start();
                    startStopSession.setText(getString(R.string.endSession));
                    biteNumberText.setText(biteNumber + "");
                    break;
                } else {
                    timeElapsedHeader.setText("Session Over!");
                    countDownText.setText("Record foods and weights");
                    biteNumber = 0;
                    chronometer.stop();
                    stopTimerTask();
                    countDownTimer.cancel();
                    startStopSession.setText(getString(R.string.startSession));
                    break;
                }
            case R.id.yummy:
                if (timerNowRunning) {
                    incrementBite();
                    stopTimerTask();
                    countDownTimer.cancel();
                    startTimerTask();
                    countDownTimer.start();
                    break;
                } else {
                    yummyButton.setText(getString(R.string.startDirections));
                }
        }
    }

    // todo: add record keeping
    // todo: add meal plan recording for session
    // todo: add email function to send to Ms. Berry

    private void incrementBite() {
        biteNumber++;
        biteNumberText.setText(biteNumber + "");
    }

    public void startTimerTask() {
        // the call from the view must be included in the signature above
        // start new session
        if (!timerNowRunning) {
            startTimer();
            timerNowRunning = !timerNowRunning;
        }
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every intervalTimeSeconds
        timer.schedule(timerTask, 0, intervalTimeSeconds * 1000); // variable values in ms.

    }

    private void sound(){

    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerNowRunning = !timerNowRunning;
        }
    }

    public class YummyCountDownTimer extends CountDownTimer {

        public YummyCountDownTimer(long startTime, long interval){
            super(startTime, interval);
        }
        @Override
        public void onFinish() {
            countDownText.setText("take a bite now");
        }

        @Override
        public void onTick(long millisUntilFinished){
            // timeElapsed = (intervalTimeSeconds * 1000) - millisUntilFinished;

            countDownText.setText( (millisUntilFinished / 1000 + 1) + "s more!");

        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
                        toast.show();
                    }
                });
            }
        };
    }
}