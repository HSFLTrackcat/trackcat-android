package com.example.finnl.gotrack.Statistics;


import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.Recording.Timer;

/*
 * This class calculates the average Speed in M/S via the selected Timer
 * */
public class SpeedAverager {
    private MainActivity creator;

    private mCounter mCounter;
    private Timer timerClass;
    private double avgSpeed = 0.0; // m/S
    private int type;


    /*
     * Init the Averager with the selected Timer
     * */
    public SpeedAverager(MainActivity creator, mCounter mCounter, Timer timerSet, int typeSet) {
        this.creator = creator;
        timerClass = timerSet;
        this.mCounter = mCounter;
        type = typeSet;
    }

    // calc avg Speed by selected Timer
    public void calcAvgSpeed() {
        double mAmount = mCounter.getAmount();
        double time = timerClass.getTime();

        avgSpeed = (mAmount / time);
    }

    // switch Timer
    public void switchTimer(Timer timerClassSet, int typeSet) {
        timerClass = timerClassSet;
        type = typeSet;
        calcAvgSpeed();
    }

    /* return current Type of Timer */
    public int getType() {
        return type;
    }

    public double getAvgSpeed(){
        return avgSpeed;
    }
}
