/**Class definition for the Scheduler Class.
 * All this Class needs to do is shuffle around Processes from state to state.
 * The Clock has its own Scheduler to continuously monitor Processes, while the
 * main program has a Scheduler that only gets used when the system is stepped
 * forward manually.
 */
package proj2;

import java.util.ArrayList;

public class Scheduler {
    
    private ArrayList<Process> allP, newP, readyP, runningP, waitingP, termP;
    private Process[] toSort;
    private int counter;
    
    public Scheduler(){
        allP = null;
        newP = null;
        readyP = null;
        runningP = null;
        waitingP = null;
        termP = null;
    }
    public void loadProcesses(ArrayList<Process> a, ArrayList<Process> np, ArrayList<Process> r){
        this.allP = a;
        this.newP = np;
        this.readyP = r;
    }
    public void sortProcessList(Process[] list){
        for(int i = 0; i < list.length-1; i++){
            String nameToTest = list[i].getName();
            int compareResult = nameToTest.compareTo(list[i].getName());
            if(compareResult > 0){
                Process temp = list[i];
                list[i] = list[i+1];
                list[i+1] = temp;
            }
        }
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
        //toSort needs reset each time the method is called
        //If Running is empty, add something to it
        if(runningP.isEmpty() == true){
            Process toAdd = readyP.get(0);
            runningP.add(toAdd);
            readyP.remove(0);
        }else{ //If a Process is in Running, check how long it's been there
            counter++; //counter = number of cycles a Process has been Running
            String runningTT = runningP.get(0).getTraceTape();
            String[] temp = runningTT.split(" ");
            ArrayList<String> splitTT = new ArrayList<>();
            for(int i = 0; i < temp.length; i++) {
                splitTT.add(temp[i]);
            } //Putting the Trace Tape into an ArrayList makes it easier to remove things
            if(counter != tq){ 
                for(int i = 1; i < splitTT.size(); i += 2) {
                    int numCycles = Integer.parseInt(splitTT.get(i));
                    if(numCycles < 0 && splitTT.get(i-1).equals("C")){
                        splitTT.remove(i);
                        if(splitTT.isEmpty()){
                            termP.add(runningP.get(0));
                            runningP.remove(0);
                            counter = 0;
                        }
                    }else{
                        numCycles--;
                        splitTT.set(i, Integer.toString(numCycles));
                        break;
                    }
                }
            }else{
                readyP.add(runningP.get(0));
                runningP.remove(0);
                counter = 0;
            }
        }
        
        //If a Process is in New, after 1 system cycle, move it to Ready
        if(newP.isEmpty() == false){
            toSort = new Process[newP.size()]; 
            for(int i = 0; i < newP.size(); i++) {
                Process toCheck = newP.get(i);
                int creationTime = toCheck.getCreationTime();
                if(creationTime+1 == ct){
                    toSort[i] = toCheck;
                }
            }
            newP.clear();
            if(toSort.length > 1){
                sortProcessList(toSort);
            }
            for(int i = 0; i < toSort.length; i++) {
                readyP.add(toSort[i]);
            }
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
}
