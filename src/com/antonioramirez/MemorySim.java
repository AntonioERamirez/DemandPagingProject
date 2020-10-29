/*
 * Filename: MemorySim.java
 * Date: 15 Dec 2019
 * Author: Antonio Ramirez
 * Purpose: Contains the algorithms for the simulation. Prints results to the console.
 *          Back bone of the program derived from https://github.com/ZacharyConlyn/demand-paging-simulator.
 */
package com.antonioramirez;

import java.util.ArrayList;
import java.util.Objects;

class MemorySim {
    private final ArrayList<Integer> referenceString;
    private final int[] removed;
    private final int[] pageCalled;
    private final boolean[] pageFault;
    private final int stringLength;
    private final int numOfPhysicalFrames;
    private final int numOfVirtualFrames;
    private final int[][] physicalMemory;
    private final Frame[] frameArray;
    private String algorithm;

    MemorySim(ArrayList<Integer> list, int pFrames, int vFrames){
        referenceString = list;
        stringLength = this.referenceString.size();
        removed = new int[stringLength];
        pageCalled = new int [stringLength];
        numOfPhysicalFrames = pFrames;
        numOfVirtualFrames = vFrames;
        physicalMemory = new int[referenceString.size()][pFrames];
        frameArray = new Frame[vFrames];
        pageFault = new boolean[stringLength];
    }

    void generate(String algo) {
        //Clear all arrays from previous simulations
        resetArrays();
        //Check current algorithm
        algorithm = algo;
        int currentRSNumber = 0;
        int frameToInsert;
        int empty;
        int frameToReplace;
        int inMemory;

        //Iterates through the entire reference string
        while (currentRSNumber < stringLength) {
            frameToInsert = referenceString.get(currentRSNumber);
            //If LRU algorithm
            if (Objects.equals(algo, "LRU")) {
                //Set last use for current frame
                frameArray[frameToInsert].setLastUse(currentRSNumber);
            } else if (Objects.equals(algo, "LFU")) {
                //Increment the times the frame has been used
                frameArray[frameToInsert].incrementTimesUsed();
            }
            empty = findIndex(physicalMemory[currentRSNumber], -1);

            inMemory = findIndex(physicalMemory[currentRSNumber], frameToInsert);
            //If already in memory
            if (inMemory != -1) {
                pageCalled[currentRSNumber] = inMemory;
                //Not a page fault
                pageFault[currentRSNumber] = false;
            }

            else if (empty >= 0) {
                pageCalled[currentRSNumber] = empty;
                physicalMemory[currentRSNumber][empty] = frameToInsert;
                frameArray[frameToInsert].setInserted(currentRSNumber);
            }

            else {
                switch (algo) {
                    case "FIFO":
                        frameToReplace = findOldest(physicalMemory[currentRSNumber]);
                        frameArray[frameToInsert].setInserted(currentRSNumber);
                        break;
                    case "OPT":
                        calculateNextUses(currentRSNumber);
                        frameToReplace = findLeastOptimal(physicalMemory[currentRSNumber]);
                        break;
                    case "LFU":
                        frameToReplace = findLfu(physicalMemory[currentRSNumber]);
                        break;
                    case "LRU":
                        frameToReplace = findLru(physicalMemory[currentRSNumber]);
                        break;
                    default:
                        System.out.println("Error in reading algorithm.");
                        return;
                }
                removed[currentRSNumber] = physicalMemory[currentRSNumber][frameToReplace];
                pageCalled[currentRSNumber] = frameToReplace;
                physicalMemory[currentRSNumber][frameToReplace] = frameToInsert;


            }
            if ((currentRSNumber + 1) < stringLength) {
                if (numOfPhysicalFrames >= 0)
                    System.arraycopy(physicalMemory[currentRSNumber], 0, physicalMemory[currentRSNumber + 1], 0, numOfPhysicalFrames);
            }
            currentRSNumber += 1;
        }
    }

    private int findOldest(int[] a) {
        int oldest = frameArray[a[0]].getInserted();
        int oldestIndex = 0;
        int checking;
        for (int i = 1; i < a.length; i++) {
            checking = frameArray[a[i]].getInserted();
            if (checking < oldest) {
                oldest = checking;
                oldestIndex = i;
            }
        }
        return oldestIndex;
    }

    private int findLfu(int[] a) {
        int lfuIndex = 0;
        int lfuTimesUsed = frameArray[a[lfuIndex]].getTimesUsed();

        for (int i = 1; i < a.length; i++) {
            int tempTimesUsed = frameArray[a[i]].getTimesUsed();

            if (tempTimesUsed < lfuTimesUsed) {
                lfuIndex = i;
                lfuTimesUsed = tempTimesUsed;
            }
        }

        return lfuIndex;
    }

    private int findLru(int[] a) {
        int lruIndex = 0;
        int lruLastUse = frameArray[a[lruIndex]].getLastUse();

        for (int i = 1; i < a.length; i++) {
            int tempLastUse = frameArray[a[i]].getLastUse();

            if (tempLastUse < lruLastUse) {
                lruIndex = i;
                lruLastUse = tempLastUse;
            }
        }
        return lruIndex;
    }

    //Checks for next use, returns the least optimal
    private int findLeastOptimal(int[] a) {
        int leastOptimal = a[0];
        int leastOptimalIndex = 0;
        int leastOptNextUse = frameArray[leastOptimal].getNextUse();
        for (int i = 1; i < a.length; i++) {
            int temp = a[i];
            int tempNextUse = frameArray[temp].getNextUse();
            if (tempNextUse > leastOptNextUse) {
                leastOptimal = temp;
                leastOptNextUse = frameArray[leastOptimal].getNextUse();
                leastOptimalIndex = i;
            }
        }
        return leastOptimalIndex;
    }

    private void calculateNextUses(int n) {
        for (int i = 0; i < numOfVirtualFrames; i++) {
            frameArray[i].setNextUse(stringLength + 1);
        }

        for (int i = stringLength - 1; i >= n; i--) {
            int called = referenceString.get(i);
            frameArray[called].setNextUse(i);
        }
    }

    //Used to clear arrays, allows the program to run different algorithms correctly
    private void resetArrays() {
        for (int i = 0; i < pageFault.length; i++) {
            pageFault[i] = true;
        }
        for (int i = 0; i < removed.length; i++) {
            removed[i] = -1;
        }
        for (int i = 0; i < pageCalled.length; i++) {
            pageCalled[i] = -1;
        }
        for (int i = 0; i < numOfVirtualFrames; i++) {
            frameArray[i] = new Frame(i);
        }
        for (int i = 0; i < stringLength; i++) {
            for (int j = 0; j < numOfPhysicalFrames; j ++) {
                physicalMemory[i][j] = -1;
            }
        }
        algorithm = "";
    }

    //Outputting results to the console, per rubric
    void print() {
        //Header showing which algorithm is being simulated
        System.out.println("*****Simulating " + algorithm + "*****");
        System.out.println();
        //Printing the reference string
        Main.validateReferenceString(referenceString);
        System.out.println();
        int iteration = 0;
        int frameNum;
        int removedInt;
        int faultCounter = 0;

        //Iterating through the entire reference string
        while (iteration < stringLength) {
            System.out.println();
            //Printing the current iteration
            System.out.println("Iteration " + iteration + ": ");
            //Printing what frame is called. Makes it easier to track the changes.
            System.out.println("Virtual frame called: " + referenceString.get(iteration));
            //Printing the current physical frames
            System.out.print("[");
            for (int i = 0; i < numOfPhysicalFrames; i++){
                frameNum = physicalMemory[iteration][i];
                if (i == 0){
                    System.out.print(frameNum);
                } else if (frameNum == -1){
                    //do nothing
                } else {
                    System.out.print(", " + frameNum);
                }
            }
            System.out.println("]");
            removedInt = removed[iteration];
            //If there was a fault, increment the counter
            if(pageFault[iteration]){
                faultCounter++;
            }
            System.out.println("Page fault: " + (pageFault[iteration] ? "Yes." : "No."));
            //Display what was replaced
            System.out.println("Victim frame: " + (removedInt == -1 ? "None." : removedInt));
            iteration += 1;
        }
        System.out.println();
        //Display the total faults
        System.out.println("Total page faults: " + faultCounter);
        System.out.println("Simluation finished.");
        System.out.println();
    }

    private int findIndex(int[] a, int n) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == n) {
                return i;
            }
        }
        return -1;
    }
}
