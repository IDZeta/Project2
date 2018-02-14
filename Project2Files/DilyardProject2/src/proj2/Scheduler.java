/**Class definition for the Scheduler Class.
 * All this Class needs to do is shuffle around Processes from state to state.
 * The Clock has its own Scheduler to continuously monitor Processes, while the
 * main program has a Scheduler that only gets used when the system is stepped
 * forward manually.
 */
package proj2;

import java.util.*;

public class Scheduler {
    
    private ArrayList<Process> allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList, listToSort;
    private int counter;
    
    public Scheduler(){
        allProcesses = null;
        newProcessList = null;
        readyProcessList = null;
        runningProcessList = null;
        waitingProcessList = null;
        termProcessList = null;
        listToSort = new ArrayList<Process>();
    }
    public void loadProcesses(ArrayList<Process> a, ArrayList<Process> n, ArrayList<Process> r, 
            ArrayList<Process> run, ArrayList<Process> w, ArrayList<Process> t){
        this.allProcesses = a;
        this.newProcessList = n;
        this.readyProcessList = r;
        this.runningProcessList = run;
        this.waitingProcessList = w;
        this.termProcessList = t;
    }
    public void checkProcessStatus(int ct){
        //If a Process is in New, after 1 system cycle, move it to Ready
        if(newProcessList.isEmpty() == false){
            
            for(int i = 0; i < newProcessList.size(); i++) {
                Process toCheck = newProcessList.get(i);
                int creationTime = toCheck.getCreationTime();
                
                if(creationTime+1 == ct){
                    readyProcessList.add(toCheck);   
                }
            }
            newProcessList.clear();
        }
        //If a Process's creation time matches the current time, move it to New
        for(int i = 0; i < allProcesses.size(); i++) {
            Process toCheck = allProcesses.get(i);
            int creationTime = toCheck.getCreationTime();
            
            if(creationTime == ct){
                newProcessList.add(toCheck);
            }
        }
    }
    public void checkProcessStatusTemp(int ct, int tq){
        //If nothing is Running, add something to Running
        if(readyProcessList.isEmpty() == false && runningProcessList.isEmpty() == true){
            runningProcessList.add(readyProcessList.get(0));
            readyProcessList.remove(0);
        }
        //If not, check how long the Process has been Running
        if(runningProcessList.isEmpty() == false){
            counter++; //counter = number of cycles a Process has been Running
            System.out.println("A Process has been Running for "+counter+" cycles");
            String runningTraceTape = runningProcessList.get(0).getTraceTape();
            String[] tempTraceTapeCharacters = runningTraceTape.split(" ");
            ArrayList<String> splitTraceTape = new ArrayList<>();
            for(int i = 0; i < tempTraceTapeCharacters.length; i++) {
                splitTraceTape.add(tempTraceTapeCharacters[i]);
            } //Putting the Trace Tape into an ArrayList makes it easier to remove things
            if(counter != tq){ //If the Time Quantum hasn't expired, check the Trace Tape
                int numCycles = Integer.parseInt(splitTraceTape.get(1));
                System.out.println("numCycles "+numCycles);
                if(numCycles < 0){ //If a C section is finished, remove it
                    splitTraceTape.remove(1);
                    splitTraceTape.remove(0);
                    //Now check what's left and move the Process accordingly
                    if(splitTraceTape.isEmpty()){ //If the Trace Tape is empty, go to Terminated
                        termProcessList.add(runningProcessList.get(0));
                        runningProcessList.remove(0);
                        counter = 0;
                    }else if(splitTraceTape.get(0).equals("I")){ //If not, go to Waiting
                        //Update the Trace Tape first
                        Process tempP = runningProcessList.get(0);
                        String TTUpdate = splitTraceTape.get(0)+" ";
                        for(int i = 1; i < splitTraceTape.size(); i++){
                            TTUpdate += splitTraceTape.get(i);
                            if(i != splitTraceTape.size()){
                                TTUpdate += " ";
                            }
                        }
                        tempP.setTraceTape(TTUpdate);
                        waitingProcessList.add(tempP);
                        runningProcessList.remove(0);
                        counter = 0;
                    }
                }else{ //If not, decrement the number of cycles left
                    numCycles--;
                    //Update the Trace Tape first
                    splitTraceTape.set(1, Integer.toString(numCycles));
                    String TTUpdate = splitTraceTape.get(0)+" ";
                    for(int i = 1; i < splitTraceTape.size(); i++){
                        TTUpdate += splitTraceTape.get(i);
                        if(i != splitTraceTape.size()){
                            TTUpdate += " ";
                        }
                    }
                    runningProcessList.get(0).setTraceTape(TTUpdate);
                }
            }else{ //If the Time Quantum has expired, move it back to Ready
                
                //Logic for moving back to Ready goes here...
            }
        }
        
        //Logic for moving out of Waiting goes here...
         
        //If a Process is in New, after 1 system cycle, move it to Ready
        if(newProcessList.isEmpty() == false){ 
            for(int i = 0; i < newProcessList.size(); i++) {
                Process processToCheck = newProcessList.get(i);
                int creationTime = processToCheck.getCreationTime();
                if(creationTime+1 == ct){
                    listToSort.add(processToCheck);
                }
            }
            newProcessList.clear();
        }
        //If a Process's creation time matches the current time, move it to New
        for(int i = 0; i < allProcesses.size(); i++) {
            Process toCheck = allProcesses.get(i);
            int creationTime = toCheck.getCreationTime();
            if(creationTime == ct){
                newProcessList.add(toCheck);
            }
        }
        //Sort everything moving into Ready
        if(listToSort.isEmpty() == false){
            System.out.println("A Process is entering Ready");
            Collections.sort(listToSort); //Sort Processes entering Ready by Creation Time
            readyProcessList.addAll(listToSort);
            listToSort.clear();
        }
        else if(listToSort.size() == 1){
            readyProcessList.add(listToSort.get(0));
        }
    }
}
