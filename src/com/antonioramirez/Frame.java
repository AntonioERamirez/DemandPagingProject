/*
 * Filename: Frame.java
 * Date: 15 Dec 2019
 * Author: Antonio Ramirez
 * Purpose: General class for frames. Contains methods to track various aspects of the frame easily. Helper for algorithms.
 *          Back bone of the program derived from https://github.com/ZacharyConlyn/demand-paging-simulator.
 */
package com.antonioramirez;

class Frame {
    private int number;
    private int inserted;
    private int nextUse;
    private int lastUse;
    private int timesUsed;

    //Parameterized constructor, frame number, sets fields to -1 (null) or 0
    Frame(int n) {
        number = n;
        inserted = -1;
        nextUse = -1;
        lastUse = -1;
        timesUsed = 0;
    }

    //Getters and Setters to track usage.
    void setInserted(int n) {
        inserted = n;
    }
    int getInserted() {
        return inserted;
    }
    void setNextUse(int n) {
        nextUse = n;
    }
    int getNextUse() {
        return nextUse;
    }
    void setLastUse(int n) {
        lastUse = n;
    }
    int getLastUse() {
        return lastUse;
    }
    void incrementTimesUsed() {
        timesUsed += 1;
    }
    int getTimesUsed() {
        return timesUsed;
    }
}
