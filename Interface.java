/**The Interface.
 * The user enters process data into the main window, then reads it into memory.
 * The system checks to see if the data is formatted correctly; if not, it won't
 * be read in.
 * The user can either let the clock run on its own, or manually progress it.
 * The state of the system is constantly saved, but is only visible when the
 * user wants to see it.
 * Created by Ian Dilyard for CSC 370 on 1/22/17. Unless specified elsewhere,
 * all code here is original. Credit to Dr. Klayder for helping debug this program.
 */
package proj2;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Interface extends javax.swing.JFrame {
    private Clock theClock;
    private ArrayList<Process> allP, newP, readyP, runningP, waitingP, termP;
    private Boolean isRunning;
    private int timeQuantum, currentTime;
    private String[] splitText;
    private int counter;
    private Scheduler theScheduler;
    
    String sampleInput = "4\n"+"2\n"+"A\n"+"C 5 I 9 C 6 I 7 C 2\n"+"7\n"+"B\n"
            + "C 12 I 33 C 15";
    //Because copying and pasting the sample code in every time is too much work.
    
    public Interface() {
        initComponents();
        isRunning = false;
        theClock = new Clock(clockDisplay);
        inputArea.setText(sampleInput);
        outputArea.setText("Output will appear here!");
        allP = new ArrayList<Process>();
        newP = new ArrayList<Process>();
        readyP = new ArrayList<Process>();
        runningP = new ArrayList<Process>();
        waitingP = new ArrayList<Process>();
        termP = new ArrayList<Process>();
        theScheduler = new Scheduler();
        //This Scheduler is only called when the system is manually stepped forward
        //While running on its own, the Clock's Scheduler handles the Processes
    }
    public void getSystemTime(){
        currentTime = theClock.getCurrentTime();
    }
    public Boolean isInputDataValid(){
        Boolean valid = false;
        counter = 0;
        //First, remove any empty slots created by extra lines
        for(int i = 0; i < splitText.length; i++){
            if(splitText[i].isEmpty()){
                System.out.println("\tEmpty slot in Process Array");
                for(int j = i; j < splitText.length-1; j++){
                    splitText[j] = splitText[j+1];
                }
                counter++; //Save the number of slots entries are shifted up
            }
        }
        try{ //Check if the Time Quantum is a number
            Integer.parseInt(splitText[0]);
            valid = true;
        }catch(NumberFormatException e){
            outputArea.append("You entered a non-numeric Time Quantum\n");
            valid = false;
        }
        for(int i = 1; i < splitText.length-counter; i += 3){
            try{ //Check the Data next
                Integer.parseInt(splitText[i]); //Check Creation Time
                valid = true;
            }catch(NumberFormatException e){
                outputArea.append("A processs you entered had a non-numeric creation time\n");
                valid = false;
                break;
            } //Now check the Strings
            if(splitText[i+1].isEmpty() == true){ //Check Name
                valid = false;
                break;
            }
            if(splitText[i+2].isEmpty() == true){ //Check Trace Tape
                valid = false;
                outputArea.append("A process you entered did not have a Trace Tape\n");
                break;
            }else{ //If the Trace Tape has data, split it and check formatting
                String[] splitTT = splitText[i+2].split(" ");
                //First, check for and remove empty slots created by extra spaces
                for(int j = 0; j < splitTT.length; j++){
                    if(splitTT[j].isEmpty()){
                        for(int k = j; k < splitTT.length-1; k++){
                            splitTT[k] = splitTT[k+1];
                        }
                    }
                } //Now check the Trace Tape contents
                for(int j = 0; j < splitTT.length; j++){ 
                    try{ //Check for ints in splitTT first 
                        Integer.parseInt(splitTT[j]);
                        if(j%2 == 0){ //Ints should be in odd-numbered locations
                            outputArea.append("An integer in the Trace Tape is in the wrong place\n");
                            valid = false;
                            break;
                        }
                        valid = true;
                    }catch(NumberFormatException e){ //Check if Strings are C or I
                        if(splitTT[j].equals("C") && j%4 == 0){
                            valid = true; //C's slot will always be divisible by 4
                        }else if(splitTT[j].equals("I")){
                            valid = true; //If it's not C, it's probably I
                        }else{
                            outputArea.append("Trace Tape can only contain C, I, or integers\n");
                            valid = false; //If not, the whole Tape is wrong
                            break;
                        }
                    }
                }
            }
        } //That was exhausting... Good thing we only have to do this once
        return valid;
        //The Try/Catch model for this code was found here:
        //https://study.com/academy/lesson/how-to-check-if-a-string-is-an-integer-in-java.html
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        inputArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        instructionsPanel = new javax.swing.JTextArea();
        clockDisplay = new javax.swing.JLabel();
        readDataButton = new javax.swing.JButton();
        runPauseButton = new javax.swing.JButton();
        statusButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        clockStepButton = new javax.swing.JButton();
        currentTimeLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        inputArea.setColumns(20);
        inputArea.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        inputArea.setRows(5);
        jScrollPane1.setViewportView(inputArea);

        instructionsPanel.setEditable(false);
        instructionsPanel.setColumns(20);
        instructionsPanel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        instructionsPanel.setLineWrap(true);
        instructionsPanel.setRows(5);
        instructionsPanel.setText("Instructions:\nEnter each piece of data on a new line. Enter the time quantum first, then input the data for each process in the following format. An example of this format is in the upper text field:\n<process creation time>\n<process name>\n<trace tape>\nAfter entering all processes, hit \"Read Data\". To start or stop the system clock, hit \"Run/Pause\". To manually step the clock forward once, hit \"Advance Clock Once\". To check the state of the system, his \"Check System Status\". To completely reset the system (clock and memory), hit \"Reset System\".");
        instructionsPanel.setWrapStyleWord(true);
        jScrollPane2.setViewportView(instructionsPanel);

        clockDisplay.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        clockDisplay.setText("0");

        readDataButton.setText("Read Data");
        readDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readDataButtonActionPerformed(evt);
            }
        });

        runPauseButton.setText("Run/Pause");
        runPauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runPauseButtonActionPerformed(evt);
            }
        });

        statusButton.setText("Show System Status");
        statusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusButtonActionPerformed(evt);
            }
        });

        outputArea.setEditable(false);
        outputArea.setColumns(20);
        outputArea.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        outputArea.setRows(5);
        jScrollPane3.setViewportView(outputArea);

        clockStepButton.setText("Advance Clock Once");
        clockStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clockStepButtonActionPerformed(evt);
            }
        });

        currentTimeLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        currentTimeLabel.setText("Current Time:");

        resetButton.setText("Reset System");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(readDataButton)
                                .addGap(57, 57, 57)
                                .addComponent(currentTimeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(clockDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(runPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clockStepButton)
                            .addComponent(statusButton)
                            .addComponent(resetButton))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(readDataButton)
                            .addComponent(currentTimeLabel)
                            .addComponent(clockDisplay))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(runPauseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clockStepButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void readDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readDataButtonActionPerformed
        String input = inputArea.getText();
        splitText = input.split("\n");
        if(isInputDataValid() == true){ //isInputDataValid() does ALL sanity checks at once
            outputArea.setText("The data you entered is formatted correctly\n");
            timeQuantum = Integer.parseInt(splitText[0]);
            //If any slots were shifted up, ignore slots at the end
            for(int i = 1; i < splitText.length-counter; i += 3){
                int processCT = Integer.parseInt(splitText[i])+currentTime;
                //If the user reads Processes in after the system has run for a
                //while, make sure their creation time hasn't been passed yet
                String processName = splitText[i+1];
                String processTT = splitText[i+2];
                allP.add(new Process(processCT, processName, processTT)); 
            }
            outputArea.setText("Input data successfully read into memory\n");
        }
    }//GEN-LAST:event_readDataButtonActionPerformed
    private void runPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runPauseButtonActionPerformed
        if(allP.isEmpty() == false){
            if(isRunning == false){
                outputArea.append("Clock started\n");
                theClock.prepareScheduler(allP, newP, readyP);
                //Loads the current states of the Processes into the Clock's Scheduler
                theClock.startClock();
                isRunning = true;
            }else{
                outputArea.append("Clock stopped\n");
                theClock.stopClock();
                isRunning = false;
            }
        }else{
            outputArea.append("No data was entered\n");
        }
    }//GEN-LAST:event_runPauseButtonActionPerformed
    private void statusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusButtonActionPerformed
        if(allP.isEmpty() == false){
            outputArea.setText("\tSystem Status Report\n");
            outputArea.append("Time Quantum: "+Integer.toString(timeQuantum)+"\n");
            getSystemTime();
            outputArea.append("Current System Time: "+Integer.toString(currentTime)+"\n");
            outputArea.append("All "+allP.size()+" Processes:\n");
            for(int i = 0; i < allP.size(); i++) {
                outputArea.append("\t"+allP.get(i).toString()+"\n");
            } //Prints every Process
            outputArea.append(newP.size()+" New Processes:\n");
            if(newP.isEmpty() == false){
                for(int i = 0; i < newP.size(); i++) {
                    outputArea.append("\t"+newP.get(i).toString()+"\n");
                } //Prints every Process in New state
            }else{
                outputArea.append("\n");
            }
            outputArea.append(readyP.size()+" Processes Ready:\n");
            if(readyP.isEmpty() == false){
                for(int i = 0; i < readyP.size(); i++) {
                    outputArea.append("\t"+readyP.get(i).toString()+"\n");
                } //Prints every Process in Ready state
            }else{
                outputArea.append("\n");
            }
            outputArea.append("Currently Running Process:\n");
            if(runningP.isEmpty() == false){ //Prints the Process in Running
                outputArea.append(runningP.get(0).toString());
            }else{
                outputArea.append("\n");
            }
            outputArea.append(waitingP.size()+" Processes Waiting:\n");
            if(waitingP.isEmpty() == false){
                for(int i = 0; i < waitingP.size(); i++) {
                    outputArea.append("\t"+waitingP.get(i).toString()+"\n");
                }
            }else{
                outputArea.append("\n");
            }
            outputArea.append(termP.size()+" Processes Completed:\n");
            if(termP.isEmpty() == false){
                for(int i = 0; i < termP.size(); i++) {
                    outputArea.append("\t"+termP.get(i).toString()+"\n");
                } //Prints all Terminated Processes
            }else{
                outputArea.append("\n");
            }
            outputArea.append("\n");
        }else{
            outputArea.append("No data read into memory\n");
        }
    }//GEN-LAST:event_statusButtonActionPerformed
    private void clockStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clockStepButtonActionPerformed
        if(allP.isEmpty() == false){
            outputArea.setText("Clock advanced one cycle\n");
            theClock.incrementTime();
            getSystemTime();
            clockDisplay.setText(Integer.toString(currentTime));
            theScheduler.loadProcesses(allP, newP, readyP);
            theScheduler.checkProcessStatus(currentTime);
        }else{
            outputArea.append("No data read into memory\n");
        } 
    }//GEN-LAST:event_clockStepButtonActionPerformed
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        inputArea.setText(sampleInput);
        outputArea.setText("System reset");
        allP.clear();
        newP.clear();
        readyP.clear();
        isRunning = false;
        theClock.stopClock();
        theClock.setCurrentTime(0);
        currentTime = 0;
        clockDisplay.setText("0");
    }//GEN-LAST:event_resetButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel clockDisplay;
    private javax.swing.JButton clockStepButton;
    private javax.swing.JLabel currentTimeLabel;
    private javax.swing.JTextArea inputArea;
    private javax.swing.JTextArea instructionsPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JButton readDataButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runPauseButton;
    private javax.swing.JButton statusButton;
    // End of variables declaration//GEN-END:variables
}
