/**Class definition for the Clock Class.
 * A thread that acts like a clock for our system. All the usual getters and setters
 * are here, as well as some other methods to start and stop the clock. To constantly
 * move Processes from state to state, the clock uses a Scheduler. This can be
 * called continuously while the clock runs, or it can be called once by calling
 * incrementTime().
 * Code written by Ian Dilyard, Seth Rhodes, and Tychell Nichols for CSC 370.
 * Project due 2/23/17. Unless specified elsewhere, all code here is original.
 * Base code taken from:
 * http://raider.mountunion.edu/csc/CSC370/Spring2018/projects/project01/ClockStarter.java
 */
package proj2;

import java.util.ArrayList;
import javax.swing.JLabel;

public class Clock implements Runnable{
    
    private Thread thread;
    private int currentTime, clockSpeed, timeQuantum;
    private Boolean paused;
    private JLabel label;
    private ArrayList<Process> allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList;
    private Simulator theScheduler;
    private Interface processInterface;
    
    public Clock(JLabel theLabel, Interface i){  
        currentTime = 0;
        label = theLabel;
        processInterface = i;
        theScheduler = new Simulator();
        clockSpeed = 1000;
        paused = true;
        timeQuantum = 0;
        thread = new Thread(this);
        thread.start();
    }
    public void run(){
        while (true){  
            if(paused == false){
                incrementTime();
                label.setText(Integer.toString(currentTime));
                processInterface.setStateDiagramValues();
            }
            try{ Thread.sleep(clockSpeed); } catch (Exception e) {}
        }
    }
    public void setClockSpeed(int speed){
        clockSpeed = speed;
    }
    public void incrementTime(){
        theScheduler.loadProcesses(allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList);
        currentTime++;
        theScheduler.checkProcessStatusTemp(currentTime, timeQuantum);
    }
    public int getCurrentTime(){
        return currentTime;
    }
    public void setCurrentTime(int ct){
        currentTime = ct;
    }
    public void startClock(){
        paused = false;
    }
    public void stopClock(){
        paused = true;
    }
    public Boolean isPaused(){
        return paused;
    }
    public void prepareScheduler(ArrayList<Process> a, ArrayList<Process> n, ArrayList<Process> r, 
            ArrayList<Process> run, ArrayList<Process> w, ArrayList<Process> t, int tq){
        allProcesses = a;
        newProcessList = n;
        readyProcessList = r;
        runningProcessList = run;
        waitingProcessList = w;
        termProcessList = t;
        timeQuantum = tq;
    }
    public static void main(String args[]) 
    {
        System.out.println("ClockStarter test code");
        //simpleClock theClock = new simpleClock();  
    }
}
