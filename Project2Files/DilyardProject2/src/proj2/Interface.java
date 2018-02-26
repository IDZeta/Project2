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
    private ArrayList<Process> allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList;
    private Boolean isRunning;
    private int timeQuantum, currentTime;
    private String[] splitText;
    private int counter;
    
    String sampleInput = "4\n"+"2\n"+"A\n"+"C 5 I 9 C 6 I 7 C 2\n"+"7\n"+"B\n"
            + "C 12 I 33 C 15";
    //Because copying and pasting the sample code in every time is too much work.
    
    public Interface() {
        initComponents();
        isRunning = false;
        theClock = new Clock(clockDisplay, this);
        inputArea.setText(sampleInput);
        outputArea.setText("Output will appear here!");
        allProcesses = new ArrayList<Process>();
        newProcessList = new ArrayList<Process>();
        readyProcessList = new ArrayList<Process>();
        runningProcessList = new ArrayList<Process>();
        waitingProcessList = new ArrayList<Process>();
        termProcessList = new ArrayList<Process>();
        
        cmbMultipleStatesEnteringWaiting.removeAllItems();
        cmbMultipleStatesEnteringWaiting.addItem("NRW");
        cmbMultipleStatesEnteringWaiting.addItem("NWR");
        cmbMultipleStatesEnteringWaiting.addItem("WNR");
        cmbMultipleStatesEnteringWaiting.addItem("WRN");
        
        cmbAdmittedSameTime.removeAllItems();
        cmbAdmittedSameTime.addItem("dataOrder");
        cmbAdmittedSameTime.addItem("dataOrderReverse");
        
        cmbExitingWaitingSameTime.removeAllItems();
        cmbExitingWaitingSameTime.addItem("dataOrder");
        cmbExitingWaitingSameTime.addItem("dataOrderReverse");
    }
    
    public void getSystemTime(){
        currentTime = theClock.getCurrentTime();
    }
    public void clearAllLists(){
        allProcesses.clear();
        newProcessList.clear();
        readyProcessList.clear();
        runningProcessList.clear();
        waitingProcessList.clear();
        termProcessList.clear();
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
        }
        catch(NumberFormatException e){
            outputArea.append("You entered a non-numeric Time Quantum\n");
            valid = false;
        }
        if(valid = true){
            for(int i = 1; i < splitText.length-counter; i += 3){
                try{ //Check the Data next
                    Integer.parseInt(splitText[i]); //Check Creation Time
                    valid = true;
                }
                catch(NumberFormatException e){
                    outputArea.append("A processs you entered had a non-numeric creation time\n");
                    valid = false;
                    break;
                }
                //Now check the Strings
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
                    }
                    //Now check the Trace Tape contents
                    for(int j = 0; j < splitTT.length; j++){ 
                        try{ //Check for ints in splitTT first 
                            Integer.parseInt(splitTT[j]);
                            if(j%2 == 0){ //Ints should be in odd-numbered locations
                                outputArea.append("An integer in the Trace Tape is in the wrong place\n");
                                valid = false;
                                break;
                            }
                            valid = true;
                        }
                        catch(NumberFormatException e){ //Check if Strings are C or I
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
        }
        return valid;
        //The Try/Catch model for this code was found here:
        //https://study.com/academy/lesson/how-to-check-if-a-string-is-an-integer-in-java.html
    }
    
    public void setStateDiagramValues()
    {  
        newProcessTextArea.setText("");
        readyTextArea.setText("");
        runningTextArea.setText("");
        waitingTextArea.setText("");
        terminatedTextArea.setText("");
        
        for(int i = 0; i < newProcessList.size(); i++)
        {
            newProcessTextArea.append(newProcessList.get(i).toString() + "\n");
        }
        for(int i = 0; i < readyProcessList.size(); i++)
        {
            readyTextArea.append(readyProcessList.get(i).toString() + "\n");
        }
        for(int i = 0; i < runningProcessList.size(); i++)
        {
            runningTextArea.append(runningProcessList.get(i).toString() + "\n");
        }
        for(int i = 0; i < waitingProcessList.size(); i++)
        {
            waitingTextArea.append(waitingProcessList.get(i).toString() + "\n");
        }
        for(int i = 0; i < termProcessList.size(); i++)
        {
            terminatedTextArea.append(termProcessList.get(i).toString() + "\n");
        }
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
        clockDisplay = new javax.swing.JLabel();
        readDataButton = new javax.swing.JButton();
        runPauseButton = new javax.swing.JButton();
        statusButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        clockStepButton = new javax.swing.JButton();
        currentTimeLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        lblInput = new javax.swing.JLabel();
        lblOutput = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        newProcessTextArea = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        readyTextArea = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        terminatedTextArea = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        runningTextArea = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        waitingTextArea = new javax.swing.JTextArea();
        lblReady = new javax.swing.JLabel();
        lblNewState = new javax.swing.JLabel();
        lblTerminated = new javax.swing.JLabel();
        lblRunning = new javax.swing.JLabel();
        lblWaiting = new javax.swing.JLabel();
        cmbMultipleStatesEnteringWaiting = new javax.swing.JComboBox();
        cmbAdmittedSameTime = new javax.swing.JComboBox();
        cmbExitingWaitingSameTime = new javax.swing.JComboBox();
        lblPromptPart1 = new javax.swing.JLabel();
        lblPromptPart2 = new javax.swing.JLabel();
        lblPromptPart3 = new javax.swing.JLabel();
        lblMultiple = new javax.swing.JLabel();
        lblIOComplete = new javax.swing.JLabel();
        lblAdmitted = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        inputArea.setColumns(20);
        inputArea.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        inputArea.setRows(5);
        jScrollPane1.setViewportView(inputArea);

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

        lblInput.setText("Input:");

        lblOutput.setText("Output:");

        newProcessTextArea.setColumns(20);
        newProcessTextArea.setRows(5);
        jScrollPane2.setViewportView(newProcessTextArea);

        readyTextArea.setColumns(20);
        readyTextArea.setRows(5);
        jScrollPane4.setViewportView(readyTextArea);

        terminatedTextArea.setColumns(20);
        terminatedTextArea.setRows(5);
        jScrollPane5.setViewportView(terminatedTextArea);

        runningTextArea.setColumns(20);
        runningTextArea.setRows(5);
        jScrollPane6.setViewportView(runningTextArea);

        waitingTextArea.setColumns(20);
        waitingTextArea.setRows(5);
        jScrollPane7.setViewportView(waitingTextArea);

        lblReady.setText("Ready State:");

        lblNewState.setText("New State:");

        lblTerminated.setText("Terminated State:");

        lblRunning.setText("Running State:");

        lblWaiting.setText("Waiting State:");

        cmbMultipleStatesEnteringWaiting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMultipleStatesEnteringWaiting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMultipleStatesEnteringWaitingActionPerformed(evt);
            }
        });

        cmbAdmittedSameTime.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbExitingWaitingSameTime.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblPromptPart1.setText("Choose how the data is ");

        lblPromptPart2.setText("ordered entering ready from");

        lblPromptPart3.setText("the boxes below:");

        lblMultiple.setText("Multiple States Entering Ready");

        lblIOComplete.setText("I/O Completed Same Time");

        lblAdmitted.setText("Admitted at Same Time");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(statusButton)
                                            .addComponent(resetButton)
                                            .addComponent(clockStepButton)
                                            .addComponent(runPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(readDataButton)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(lblPromptPart2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblPromptPart1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(lblPromptPart3)
                                            .addComponent(lblMultiple)
                                            .addComponent(cmbAdmittedSameTime, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbMultipleStatesEnteringWaiting, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblAdmitted, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(14, 14, 14)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                                    .addComponent(jScrollPane4)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblNewState, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblReady, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTerminated, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblRunning, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblIOComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblInput, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(169, 169, 169)
                                .addComponent(currentTimeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(clockDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(cmbExitingWaitingSameTime, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(142, 142, 142)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblWaiting, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(68, 68, 68))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblInput, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblOutput)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(currentTimeLabel)
                                    .addComponent(clockDisplay))
                                .addGap(16, 16, 16))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(readDataButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNewState)
                            .addComponent(lblTerminated)
                            .addComponent(runPauseButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblReady)
                                    .addComponent(lblRunning)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(clockStepButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton)
                                .addGap(11, 11, 11)
                                .addComponent(lblPromptPart1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPromptPart2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPromptPart3)
                                .addGap(33, 33, 33)
                                .addComponent(lblMultiple)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbMultipleStatesEnteringWaiting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblAdmitted)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbAdmittedSameTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4))
                        .addGap(8, 8, 8)
                        .addComponent(lblIOComplete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblWaiting)
                            .addComponent(cmbExitingWaitingSameTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void readDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readDataButtonActionPerformed
        String input = inputArea.getText();
        newProcessTextArea.setText("");
        readyTextArea.setText("");
        runningTextArea.setText("");
        waitingTextArea.setText("");
        terminatedTextArea.setText("");
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
                allProcesses.add(new Process(processCT, processName, processTT)); 
            }
            
            outputArea.setText("Input data successfully read into memory\n");
        }
        //setStateDiagramValues();
    }//GEN-LAST:event_readDataButtonActionPerformed
    private void runPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runPauseButtonActionPerformed
        if(allProcesses.isEmpty() == false){
            
            if(isRunning == false){
                outputArea.append("Clock started\n");
                theClock.prepareScheduler(allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList, timeQuantum);
                //Loads the current states of the Processes into the Clock's Scheduler
                theClock.startClock();
                isRunning = true;
            }
            else{
                outputArea.append("Clock stopped\n");
                theClock.stopClock();
                isRunning = false;
            }
        }
        else{
            outputArea.append("No data was entered\n");
        }
    }//GEN-LAST:event_runPauseButtonActionPerformed
    private void statusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusButtonActionPerformed
        if(allProcesses.isEmpty() == false){
            outputArea.append("\tSystem Status Report\n");
            outputArea.append("Time Quantum: "+Integer.toString(timeQuantum)+"\n");
            getSystemTime();
            outputArea.append("Current System Time: "+Integer.toString(currentTime)+"\n");
            outputArea.append("All "+allProcesses.size()+" Processes:\n");
            for(int i = 0; i < allProcesses.size(); i++) {
                outputArea.append("\t"+allProcesses.get(i).toString()+"\n");
            } //Prints every Process
            outputArea.append(newProcessList.size()+" New Processes:\n");
            if(newProcessList.isEmpty() == false){
                for(int i = 0; i < newProcessList.size(); i++) {
                    outputArea.append("\t"+newProcessList.get(i).toString()+"\n");
                } //Prints every Process in New state
            }else{
                outputArea.append("\n");
            }
            outputArea.append(readyProcessList.size()+" Processes Ready:\n");
            if(readyProcessList.isEmpty() == false){
                for(int i = 0; i < readyProcessList.size(); i++) {
                    outputArea.append("\t"+readyProcessList.get(i).toString()+"\n");
                } //Prints every Process in Ready state
            }else{
                outputArea.append("\n");
            }
            outputArea.append("Currently Running Process:\n");
            if(runningProcessList.isEmpty() == false){ //Prints the Process in Running
                outputArea.append(runningProcessList.get(0).toString());
            }else{
                outputArea.append("\n");
            }
            outputArea.append(waitingProcessList.size()+" Processes Waiting:\n");
            if(waitingProcessList.isEmpty() == false){
                for(int i = 0; i < waitingProcessList.size(); i++) {
                    outputArea.append("\t"+waitingProcessList.get(i).toString()+"\n");
                }
            }else{
                outputArea.append("\n");
            }
            outputArea.append(termProcessList.size()+" Processes Completed:\n");
            if(termProcessList.isEmpty() == false){  
                for(int i = 0; i < termProcessList.size(); i++) {
                    outputArea.append("\t"+termProcessList.get(i).toString()+"\n");
                } //Prints all Terminated Processes
            }else{
                outputArea.append("\n");
            } 
        }else{
            outputArea.append("No data read into memory\n");
        }
    }//GEN-LAST:event_statusButtonActionPerformed
    private void clockStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clockStepButtonActionPerformed
        if(allProcesses.isEmpty() == false){
            outputArea.setText("Clock advanced one cycle\n");
            theClock.prepareScheduler(allProcesses, newProcessList, readyProcessList, runningProcessList, waitingProcessList, termProcessList, timeQuantum);
            theClock.incrementTime();
            getSystemTime();
            clockDisplay.setText(Integer.toString(currentTime));
            setStateDiagramValues();
        }else{
            outputArea.append("No data read into memory\n");
        } 
        
    }//GEN-LAST:event_clockStepButtonActionPerformed
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        inputArea.setText(sampleInput);
        newProcessTextArea.setText("");
        readyTextArea.setText("");
        runningTextArea.setText("");
        waitingTextArea.setText("");
        terminatedTextArea.setText("");
        outputArea.setText("System reset\n");
        clearAllLists();
        isRunning = false;
        theClock.stopClock();
        theClock.setCurrentTime(0);
        currentTime = 0;
        clockDisplay.setText("0");
    }//GEN-LAST:event_resetButtonActionPerformed

    private void cmbMultipleStatesEnteringWaitingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMultipleStatesEnteringWaitingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMultipleStatesEnteringWaitingActionPerformed
    
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
    private javax.swing.JComboBox cmbAdmittedSameTime;
    private javax.swing.JComboBox cmbExitingWaitingSameTime;
    private javax.swing.JComboBox cmbMultipleStatesEnteringWaiting;
    private javax.swing.JLabel currentTimeLabel;
    private javax.swing.JTextArea inputArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lblAdmitted;
    private javax.swing.JLabel lblIOComplete;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblMultiple;
    private javax.swing.JLabel lblNewState;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblPromptPart1;
    private javax.swing.JLabel lblPromptPart2;
    private javax.swing.JLabel lblPromptPart3;
    private javax.swing.JLabel lblReady;
    private javax.swing.JLabel lblRunning;
    private javax.swing.JLabel lblTerminated;
    private javax.swing.JLabel lblWaiting;
    private javax.swing.JTextArea newProcessTextArea;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JButton readDataButton;
    private javax.swing.JTextArea readyTextArea;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runPauseButton;
    private javax.swing.JTextArea runningTextArea;
    private javax.swing.JButton statusButton;
    private javax.swing.JTextArea terminatedTextArea;
    private javax.swing.JTextArea waitingTextArea;
    // End of variables declaration//GEN-END:variables
}
