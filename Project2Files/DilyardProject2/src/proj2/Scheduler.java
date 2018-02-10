/**Class definition for the Scheduler Class.
 * All this Class needs to do is shuffle around Processes from state to state.
 * The Clock has its own Scheduler to continuously monitor Processes, while the
 * main program has a Scheduler that only gets used when the system is stepped
 * forward manually.
 */
package proj2;

import java.util.*;

public class Scheduler {
    
    private ArrayList<Process> allP, newP, readyP, runningP, waitingP, termP, toSort;
    private int counter;
    
    public Scheduler(){
        allP = null;
        newP = null;
        readyP = null;
        runningP = null;
        waitingP = null;
        termP = null;
    }
    public void loadProcesses(ArrayList<Process> a, ArrayList<Process> n, ArrayList<Process> r, 
            ArrayList<Process> run, ArrayList<Process> w, ArrayList<Process> t){
        this.allP = a;
        this.newP = n;
        this.readyP = r;
        this.runningP = run;
        this.waitingP = w;
        this.termP = t;
    }
    public void checkProcessStatus(int ct){
        //If a Process is in New, after 1 system cycle, move it to Ready
        if(newP.isEmpty() == false){
            for(int i = 0; i < newP.size(); i++) {
                Process toCheck = newP.get(i);
                int creationTime = toCheck.getCreationTime();
                if(creationTime+1 == ct){
                    readyP.add(toCheck);   
                }
            }
            newP.clear();
        }
        //If a Process's creation time matches the current time, move it to New
        for(int i = 0; i < allP.size(); i++) {
            Process toCheck = allP.get(i);
            int creationTime = toCheck.getCreationTime();
            if(creationTime == ct){
                newP.add(toCheck);
            }
        }
    }
    public void checkProcessStatusTemp(int ct, int tq){
        //If nothing is Running, add something to Running
        if(readyP.isEmpty() == false){ //Make sure there's something to add first
            if(runningP.isEmpty() == true){
                runningP.add(readyP.get(0));
                readyP.remove(0);
            }else{ //If not, check how long the Process has been Running
                counter++; //counter = number of cycles a Process has been Running
                String runningTT = runningP.get(0).getTraceTape();
                String[] temp = runningTT.split(" ");
                ArrayList<String> splitTT = new ArrayList<>();
                for(int i = 0; i < temp.length; i++) {
                    splitTT.add(temp[i]); //An ArrayList is easier to work with
                } //Putting the Trace Tape into an ArrayList makes it easier to remove things
                if(counter != tq){ //If the Time Quantum hasn't expired, check the Trace Tape
                    int numCycles = Integer.parseInt(splitTT.get(1));
                    if(numCycles < 0){ //If a C section is finished, remove it
                        splitTT.remove(1);
                        splitTT.remove(0);
                        //Now check what's left and move the Process accordingly
                        if(splitTT.isEmpty()){ //If the Trace Tape is empty, go to Terminated
                            termP.add(runningP.get(0));
                            runningP.remove(0);
                            counter = 0;
                        }else if(splitTT.get(0).equals("I")){ //If not, go to Waiting
                            Process tempP = runningP.get(0);
                            String TTUpdate = null;
                            for(int i = 0; i < splitTT.size(); i++){
                                TTUpdate += splitTT.get(i);
                                if(i != splitTT.size()){
                                    TTUpdate += " ";
                                }
                            } //Need to update the Trace Tape first
                            tempP.setTraceTape(TTUpdate);
                            waitingP.add(tempP);
                            runningP.remove(0);
                            counter = 0;
                        }
                    }
                }else{ //If the Time Quantum has expired, move it to Ready
                    
                    //Logic for moving back to Ready goes here...
                    
                }
            }
        }
        
        //Logic for moving out of Waiting goes here...
         
        //If a Process is in New, after 1 system cycle, move it to Ready
        if(newP.isEmpty() == false){ 
            for(int i = 0; i < newP.size(); i++) {
                Process toCheck = newP.get(i);
                int creationTime = toCheck.getCreationTime();
                if(creationTime+1 == ct){
                    toSort.add(toCheck);
                }
            }
            newP.clear();
            
        }
        //If a Process's creation time matches the current time, move it to New
        for(int i = 0; i < allP.size(); i++) {
            Process toCheck = allP.get(i);
            int creationTime = toCheck.getCreationTime();
            if(creationTime == ct){
                newP.add(toCheck);
            }
        }
        //Sort everything moving into Ready
        if(toSort.length > 1){
            sortProcessList(toSort);
        }
        for(int i = 0; i < toSort.length; i++) {
            readyP.add(toSort[i]);
        }
    }
}
