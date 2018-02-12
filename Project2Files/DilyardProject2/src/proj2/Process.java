/**Class definition for the Process Class.
 * Includes all of the usual getter, setter, and toString methods.
 * Also includes a compareTo method that allows an ArrayList of Processes
 * to be sorted easily.
 * Unless specified elsewhere, all code here is original.
 */
package proj2;

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
    
    public int compareTo(Process toCompare){
        int compareTime = toCompare.getCreationTime();
        return this.creationTime - compareTime;
    }
}
