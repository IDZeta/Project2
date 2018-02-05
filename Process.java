/**Class definition for the Process Class.
 * Includes all of the usual getter, setter, and toString methods.
 * Created by Ian Dilyard for CSC 370 on 1/22/17. Unless specified elsewhere,
 * all code here is original.
 */
package proj2;

import java.util.ArrayList;

public class Process{
    
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
}
