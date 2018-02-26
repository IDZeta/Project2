/**Class definition for the Scheduler Class.
 * All this Class needs to do is shuffle around Processes from state to state.
 * The Clock class uses a Scheduler so that it can handle the Process shuffling.
 * Code written by Ian Dilyard, Seth Rhodes, and Tychell Nichols for CSC 370.
 * Project due 2/23/17. Unless specified elsewhere, all code here is original.
 * Credit to Dr. Klayder for helping debug this program.
 */
package proj2;

import java.util.*;

public class Simulator{
    
    private ArrayList<Process> allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList, listToSort;
    private int runningCounter;
    
    public Simulator(){
        allProcesses = null;
        newProcessList = null;
        readyProcessList = null;
        runningProcessList = null;
        waitingProcessList = null;
        termProcessList = null;
        listToSort = new ArrayList<Process>();
        runningCounter = -1;
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
    public void rebuildTraceTape(ArrayList<String> splitTraceTape, Process p){
        //Since we need to remake Trace Tapes so often, might as well have a method for it
        String TTUpdate = splitTraceTape.get(0)+" ";
        for(int i = 1; i < splitTraceTape.size(); i++) {
            TTUpdate += splitTraceTape.get(i);
            if(i != splitTraceTape.size()){
                TTUpdate += " ";
            }
        }
        p.setTraceTape(TTUpdate);
    }
    public void checkRunningProcess(int tq){
        //If nothing is Running, add something to Running
        if(readyProcessList.isEmpty() == false && runningProcessList.isEmpty() == true){
            runningProcessList.add(readyProcessList.get(0));
            readyProcessList.remove(0);
        }
        //If not, check how long the Process has been Running
        if(runningProcessList.isEmpty() == false){
            runningCounter++; //counter = number of cycles a Process has been Running
            System.out.println("A Process has been Running for "+runningCounter+" cycles");
            String runningTraceTape = runningProcessList.get(0).getTraceTape();
            String[] tempTraceTapeCharacters = runningTraceTape.split(" ");
            //Putting the Trace Tape into an ArrayList makes it easier to remove things
            ArrayList<String> splitTraceTape = new ArrayList<>();
            for(int i = 0; i < tempTraceTapeCharacters.length; i++) {
                splitTraceTape.add(tempTraceTapeCharacters[i]);
            }
            //If the Time Quantum hasn't expired, check the Trace Tape
            if(runningCounter != tq+1){
                int runCycles = Integer.parseInt(splitTraceTape.get(1));
                System.out.println("\t\tnumCycles "+runCycles);
                //If a "C _" block is finished, remove it
                if(runCycles <= 0){
                    System.out.println("\tThe Running Process is done using the CPU");
                    splitTraceTape.remove(1);
                    splitTraceTape.remove(0);
                    //Now check what's left of the Trace Tape and move the Process accordingly
                    //If the Trace Tape is empty, go to Terminated
                    if(splitTraceTape.isEmpty()){
                        termProcessList.add(runningProcessList.get(0));
                        runningProcessList.remove(0);
                        runningCounter = -1;
                        System.out.println("The Running Process was Terminated");
                    }else if(splitTraceTape.get(0).equals("I")){
                        //If the Trace Tape is not empty, go to Waiting
                        //Update the Trace Tape first
                        Process tempP = runningProcessList.get(0);
                        rebuildTraceTape(splitTraceTape, tempP);
                        waitingProcessList.add(tempP);
                        runningProcessList.remove(0);
                        runningCounter = -1;
                        System.out.println("\tThe Running Process moved to Waiting");
                    }
                }else{ //If not finished with the CPU, decrement the number of cycles left
                    System.out.println("\tThe Running Process needs more time");
                    if(runningCounter != 0){
                        runCycles--;
                    }
                    //Update the Trace Tape first
                    splitTraceTape.set(1, Integer.toString(runCycles));
                    rebuildTraceTape(splitTraceTape, runningProcessList.get(0));
                }
            }else{ //If the Time Quantum has expired, move it back to Ready
                System.out.println("\tThe Time Quantum has expired");
                //Update the Trace Tape first
                Process tempP = runningProcessList.get(0);
                rebuildTraceTape(splitTraceTape, tempP);
                listToSort.add(tempP);
                runningProcessList.remove(0);
                runningCounter = -1;
            }
        }
    }
    public void checkWaitingProcesses(){
         //If at least one Process is in Waiting, see if it's done Waiting
        if(waitingProcessList.isEmpty() == false){
            String waitingTraceTape = waitingProcessList.get(0).getTraceTape();
            String[] tempTraceTapeCharacters = waitingTraceTape.split(" ");
            //Putting the Trace Tape into an ArrayList makes it easier to remove things
            ArrayList<String> splitTraceTape = new ArrayList<>();
            for(int i = 0; i < tempTraceTapeCharacters.length; i++) {
                splitTraceTape.add(tempTraceTapeCharacters[i]);
            }
            //Check how long each Process has been Waiting
            for(int i = 0; i < waitingProcessList.size(); i++) {
                int waitCycles = Integer.parseInt(splitTraceTape.get(1));
                System.out.println("A Process in Waiting has "+waitCycles+" cycles left");
                if(waitCycles > 0){
                    //If an "I _" block isn't finished, decrement the time remaining
                    waitCycles--;
                    splitTraceTape.set(1, Integer.toString(waitCycles));
                    rebuildTraceTape(splitTraceTape, waitingProcessList.get(i));
                }else{ //If it IS finished, remove that block and move the Process into Ready
                    System.out.println("\tA Process is done Waiting");
                    splitTraceTape.remove(1);
                    splitTraceTape.remove(0);
                    rebuildTraceTape(splitTraceTape, waitingProcessList.get(i));
                    listToSort.add(waitingProcessList.get(i));
                    waitingProcessList.remove(i);
                    i--; //Ensures that a member of the list isn't skipped when something is removed
                }
            }
        }
        if(listToSort.size() > 1){
            Collections.sort(listToSort);
        }
    }
    public void checkNewProcesses(int ct){
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
        if(listToSort.size() > 1){
            Collections.sort(listToSort);
        }
    }
    public void checkProcesses(int ct, int tq, String order){
        if(order.equals("NRW")){
            checkNewProcesses(ct);
            checkRunningProcess(tq);
            checkWaitingProcesses();
        }
        else if(order.equals("NWR")){
            checkNewProcesses(ct);
            checkWaitingProcesses();
            checkRunningProcess(tq);
        }
        else if(order.equals("WNR")){
            checkWaitingProcesses();
            checkNewProcesses(ct);
            checkRunningProcess(tq);
        }
        else if(order.equals("WRN")){
            checkWaitingProcesses();
            checkRunningProcess(tq);
            checkNewProcesses(ct);
        }
        readyProcessList.addAll(listToSort);
        listToSort.clear();
    }
}
