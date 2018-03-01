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
    
    private ArrayList<Process> allProcesses, newProcessList, readyProcessList, 
            runningProcessList, waitingProcessList, termProcessList, enteringReadyList,
            exitingWaitingList, exitingNewList;
    private int runningCounter;
    private ArrayList<Integer> waitingArrivalTimeList;
    public String waitRule, admitRule;
    
    public Simulator(){
        enteringReadyList = new ArrayList<Process>();
        exitingWaitingList = new ArrayList<Process>();
        exitingNewList = new ArrayList<Process>();
        waitingArrivalTimeList = new ArrayList<Integer>();
        runningCounter = -1;
        waitRule = null;
        admitRule = null;
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
    public void sortList(ArrayList<Process> list, String rule){
        //This method determines how multiple Processes exiting a state are sorted
        if(list.isEmpty() == false && list.size() > 1){
            if(rule.equals("dataOrder")){
                ArrayList<Process> temp1 = new ArrayList<Process>();
                for (int i = 0; i < list.size(); i++) {
                    temp1.add(null);
                }
                for(int i = 0; i < list.size(); i++){
                    int index = list.get(i).findProcessIndex(allProcesses);
                    temp1.set(index, list.get(i));
                }
                list.clear();
                list.addAll(temp1);
            }
            else if(rule.equals("dataOrderReverse")){
                ArrayList<Process> temp2 = new ArrayList<Process>();
                for (int i = 0; i < list.size(); i++) {
                    temp2.add(null);
                }
                for(int i = 0; i < list.size(); i++){
                    int index = list.get(i).findProcessIndex(allProcesses);
                    temp2.set((list.size()-1)-index, list.get(i));
                }
                list.clear();
                list.addAll(temp2);
            }
            else if(rule.equals("creationTime")){
                Collections.sort(list);
            }
            else if(rule.equals("creationTimeReverse")){
                Collections.sort(list, Process.sortCTReverse);
            }
            else if(rule.equals("alphabetical")){
                Collections.sort(list, Process.sortAlphabetically);
            }
            else if(rule.equals("alphabeticalReverse")){
                Collections.sort(list, Process.sortAlphabeticallyReverse);
            }
        } 
    }
    public void checkRunningProcess(int tq, int ct){
        //Check how long the Process has been Running, if there is one
        if(runningProcessList.isEmpty() == false){
            runningCounter++; //counter = number of cycles a Process has been Running
            String runningTraceTape = runningProcessList.get(0).getTraceTape();
            String[] tempTraceTapeCharacters = runningTraceTape.split(" ");
            //Putting the Trace Tape into an ArrayList makes it easier to remove things
            ArrayList<String> splitTraceTape = new ArrayList<>();
            splitTraceTape.addAll(Arrays.asList(tempTraceTapeCharacters));
            int runCyclesLeft = Integer.parseInt(splitTraceTape.get(1));
            //If the Time Quantum hasn't expired, check the Trace Tape
            if(runningCounter != tq+1){
                //If a "C _" block is finished, remove it
                if(runCyclesLeft == 0){
                    splitTraceTape.remove(1);
                    splitTraceTape.remove(0);
                    //Now check what's left of the Trace Tape and move the Process accordingly
                    //If the Trace Tape is empty, go to Terminated
                    if(splitTraceTape.isEmpty()){
                        termProcessList.add(runningProcessList.get(0));
                        runningProcessList.remove(0);
                        runningCounter = -1;
                    }else if(splitTraceTape.get(0).equals("I")){
                        //If the Trace Tape is not empty, go to Waiting
                        //Update the Trace Tape first
                        Process tempP = runningProcessList.get(0);
                        rebuildTraceTape(splitTraceTape, tempP);
                        waitingProcessList.add(tempP);
                        waitingArrivalTimeList.add(ct);
                        runningProcessList.remove(0);
                        runningCounter = -1;
                    }
                }else{ //If not finished with the CPU, decrement the number of cycles left
                    if(runningCounter != 0){
                        runCyclesLeft--;
                    }
                    //Update the Trace Tape first
                    splitTraceTape.set(1, Integer.toString(runCyclesLeft));
                    rebuildTraceTape(splitTraceTape, runningProcessList.get(0));
                }
            }else{ //If the Time Quantum has expired, check where the Process needs to go
                //If the C value is 0, move it to Waiting instead of Ready
                //This occurs if the C value equals the Time Quantum, so we need to check for it...
                if(runCyclesLeft == 0){
                    splitTraceTape.remove(1);
                    splitTraceTape.remove(0);
                    //If the Trace Tape is empty, go to Terminated
                    if(splitTraceTape.isEmpty()){
                        termProcessList.add(runningProcessList.get(0));
                        runningProcessList.remove(0);
                        runningCounter = -1;
                    }else if(splitTraceTape.get(0).equals("I")){
                        //If the Trace Tape is not empty, go to Waiting
                        //Update the Trace Tape first
                        Process tempP = runningProcessList.get(0);
                        rebuildTraceTape(splitTraceTape, tempP);
                        waitingProcessList.add(tempP);
                        waitingArrivalTimeList.add(ct);
                        runningProcessList.remove(0);
                        runningCounter = -1;
                    }
                }else{
                    //Update the Trace Tape first
                    Process tempP = runningProcessList.get(0);
                    rebuildTraceTape(splitTraceTape, tempP);
                    enteringReadyList.add(tempP);
                    //Because only 1 Process can exit Running, it doesn't need sorted here
                    runningProcessList.remove(0);
                    runningCounter = -1;
                }
            }
        }
        //If nothing is Running, add something to Running
        if(readyProcessList.isEmpty() == false && runningProcessList.isEmpty() == true){
            runningProcessList.add(readyProcessList.get(0));
            readyProcessList.remove(0);
            runningCounter++;
        }
    }
    public void checkWaitingProcesses(int ct){
        //If at least one Process is in Waiting, see if it's done Waiting
        if(waitingProcessList.isEmpty() == false){
            for(int i = 0; i < waitingProcessList.size(); i++){
                String waitingTraceTape = waitingProcessList.get(i).getTraceTape();
                String[] tempTraceTapeCharacters = waitingTraceTape.split(" ");
                //Putting the Trace Tape into an ArrayList makes it easier to remove things
                ArrayList<String> splitTraceTape = new ArrayList<>();
                for(int j = 0; j < tempTraceTapeCharacters.length; j++) {
                    splitTraceTape.add(tempTraceTapeCharacters[j]);
                } 
                //Check how long each Process has been Waiting
                int waitCyclesLeft = Integer.parseInt(splitTraceTape.get(1));
                int timeArrived = waitingArrivalTimeList.get(i);
                //System.out.println("A Process in Waiting has "+waitCyclesLeft+" cycles left");
                if(waitCyclesLeft > 0){
                    //If an "I _" block isn't finished, decrement the time remaining
                    if(timeArrived-ct != 0){
                        waitCyclesLeft--;
                    }
                    splitTraceTape.set(1, Integer.toString(waitCyclesLeft));
                    rebuildTraceTape(splitTraceTape, waitingProcessList.get(i));
                }else{ //If it IS finished, remove that block and move the Process into Ready
                    splitTraceTape.remove(1);
                    splitTraceTape.remove(0);
                    rebuildTraceTape(splitTraceTape, waitingProcessList.get(i));
                    exitingWaitingList.add(waitingProcessList.get(i));
                    waitingProcessList.remove(i);
                    waitingArrivalTimeList.remove(i);
                    i--; //Ensures that a member of the list isn't skipped when something is removed
                }
            } 
        }
        sortList(exitingWaitingList, waitRule);
        enteringReadyList.addAll(exitingWaitingList);
        exitingWaitingList.clear();
    }
    public void checkNewProcesses(int ct){
        //If a Process is in New, after 1 system cycle, move it to Ready
        if(newProcessList.isEmpty() == false){ 
            for(int i = 0; i < newProcessList.size(); i++) {
                Process processToCheck = newProcessList.get(i);
                int creationTime = processToCheck.getCreationTime();
                if(creationTime+1 == ct){
                    exitingNewList.add(processToCheck);
                }
            }
            newProcessList.clear();
            sortList(exitingNewList, admitRule);
            enteringReadyList.addAll(exitingNewList);
            exitingNewList.clear();
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
    public void checkProcesses(int ct, int tq, String order){
        switch(order){ //order determines what clump of Processes is handled first
            case "NRW":
                checkNewProcesses(ct);
                checkRunningProcess(tq, ct);
                checkWaitingProcesses(ct);
                break;
            case "NWR":
                checkNewProcesses(ct);
                checkWaitingProcesses(ct);
                checkRunningProcess(tq, ct);
                break;
            case "WNR":
                checkWaitingProcesses(ct);
                checkNewProcesses(ct);
                checkRunningProcess(tq, ct);
                break;
            case "WRN":
                checkWaitingProcesses(ct);
                checkRunningProcess(tq, ct);
                checkNewProcesses(ct);
                break;
        }
        readyProcessList.addAll(enteringReadyList);
        enteringReadyList.clear();
    }
}
