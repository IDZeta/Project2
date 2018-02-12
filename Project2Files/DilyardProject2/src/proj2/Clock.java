/**Class definition for the Clock Class.
 * A thread that acts like a clock for our system. All the usual getters and setters
 * are here, as well as some other methods to start and stop the clock. To constantly
 * move Processes from state to state, the clock has its own Scheduler.
 * Base code taken from:
 * http://raider.mountunion.edu/csc/CSC370/Spring2018/projects/project01/ClockStarter.java
 */
package proj2;

import java.util.ArrayList;
import javax.swing.JLabel;

public class Clock implements Runnable{
    
    private Thread thread;
    private int currentTime;
    private int clockSpeed;
    private Boolean paused;
    private JLabel label;
    private ArrayList<Process> allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList;
    private Scheduler theScheduler;
    
    public Clock(JLabel theLabel){  
        currentTime = 0;
        label = theLabel;
        theScheduler = new Scheduler();
        clockSpeed = 1000;
        paused = true;
        thread = new Thread(this);
        thread.start();
    }
    
    public void run(){
        while (true){  
            if(paused == false){
                theScheduler.loadProcesses(allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList);
                incrementTime();
                label.setText(Integer.toString(currentTime));
                theScheduler.checkProcessStatus(currentTime);
                try{ Thread.sleep(clockSpeed); } catch (Exception e) {}
            }
        }
    }
    
    public void setClockSpeed(int speed){
        clockSpeed = speed;
    }
    
    public void incrementTime(){
        currentTime++;
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
            ArrayList<Process> run, ArrayList<Process> w, ArrayList<Process> t){
        allProcesses = a;
        newProcessList = n;
        readyProcessList = r;
        runningProcessList = run;
        waitingProcessList = w;
        termProcessList = t;
    }
    
    public static void main(String args[]) 
    {
        System.out.println("ClockStarter test code");
        //simpleClock theClock = new simpleClock();  
    }
}
