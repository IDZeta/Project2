/**Class definition for the Process Class.
 * Includes all of the usual getter, setter, and toString methods.
 * Also includes a compareTo method that allows an ArrayList of Processes
 * to be sorted easily.
 * Unless specified elsewhere, all code here is original.
 */
package proj2;

import java.util.Comparator;

public class Process implements Comparable<Process>{
    
    private int creationTime;
    private String name, traceTape;
    
    public Process (int ct, String n, String tt){
        this.creationTime = ct;
        this.name = n;
        this.traceTape = tt;
    }
    
    public int getCreationTime(){
        return this.creationTime;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getTraceTape(){
        return this.traceTape;
    }
    
    public void setCreationTime(int ct){
        this.creationTime = ct;
    }
    
    public void setName(String n){
        this.name = n;
    }
    
    public void setTraceTape(String tt){
        this.traceTape = tt;
    }
    
    public String toString(){
        String output = this.creationTime+" :: "+this.name+" :: "+this.traceTape;
        return output;
    }
    @Override
    public int compareTo(Process p1){
        int compareTime = p1.getCreationTime();
        return this.creationTime - compareTime;
        //Sorts Processes in ascending order by Creation Time
    }
    public static Comparator<Process> sortCTReverse = new Comparator<Process>(){
        @Override
        public int compare(Process p1, Process p2){
            int p1CT = p1.getCreationTime();
            int p2CT = p2.getCreationTime();
            return p2CT - p1CT;
        }
    };
    public static Comparator<Process> sortAlphabetically = new Comparator<Process>(){
        @Override
        public int compare(Process p1, Process p2) {
            String name1 = p1.getName();
            String name2 = p2.getName();
            return name1.compareTo(name2);
        }
    };
    public static Comparator<Process> sortAlphabeticallyReverse = new Comparator<Process>(){
        @Override
        public int compare(Process p1, Process p2) {
            String name1 = p1.getName();
            String name2 = p2.getName();
            return name2.compareTo(name1);
        }
    };
}
