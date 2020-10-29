/*
 * Filename: Main.java
 * Date: 15 Dec 2019
 * Author: Antonio Ramirez
 * Purpose: Contains the main method for the program. Prints a menu and contains methods to handle the 2 reference string methods.
 *          Back bone of the program derived from https://github.com/ZacharyConlyn/demand-paging-simulator.
 */
package com.antonioramirez;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Main {
    //Maxes defined in the project rubric
    private static final int VPAGEMAX = 10;
    private static final int PPAGEMAX = 7;
    //Used throughout to read user input
    private static final Scanner scanner = new Scanner(System.in);
    //Flag to determine whether a string has been loaded
    private static boolean refStringFlag = false;
    private static ArrayList<Integer> referenceString;

    public static void main(String[] args) {
        int numOfPhysicalFrames;
        while (true){
            //Setting the number of physical frames from user input.
            //Ensures it is under the limit
            System.out.println("Please enter the number of physical frames (must be < 8): ");
            int physicalFrames = Integer.parseInt(scanner.next());
            numOfPhysicalFrames = physicalFrames;

            if (physicalFrames <= PPAGEMAX){
                System.out.println("Number of physical frames set to : " + physicalFrames);
                break;
            } else {
                System.out.println("Please enter a valid amount of frames.");
            }
        }

        while (true){
            //Basic menu form rubric
            System.out.println();
            System.out.println("Please choose from the following options:");
            System.out.println("0 - Exit");
            System.out.println("1 - Read reference string");
            System.out.println("2 - Generate reference string");
            System.out.println("3 - Display current reference string");
            System.out.println("4 - Simulate FIFO");
            System.out.println("5 - Simulate OPT");
            System.out.println("6 - Simulate LRU");
            System.out.println("7 - Simulate LFU");
            System.out.println();

            String input = scanner.next();

            //MemorySim instance to run the algorithms
            MemorySim sim;
            //Switch to handle user selection
            //Checks if a string has been loaded before running an algorithm
            switch (input){
                case "0": //Exit
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                case "1": //Read user input for reference string
                    referenceString = readRefString(scanner);
                    validateReferenceString(referenceString);
                    break;
                case "2": //Randomly generated string based on user parameters
                    referenceString = generateString(scanner);
                    validateReferenceString(referenceString);
                    break;
                case "3": //Prints the string
                    if(refStringFlag){
                        validateReferenceString(referenceString);
                    } else {
                        System.out.println("No reference string loaded.");
                    }
                    break;
                case "4": //FIFO
                    if (refStringFlag) {
                        sim = new MemorySim(referenceString, numOfPhysicalFrames, VPAGEMAX);
                        sim.generate("FIFO");
                        sim.print();
                    }
                    break;
                case "5": //OPT
                    if (refStringFlag) {
                        sim = new MemorySim(referenceString, numOfPhysicalFrames, VPAGEMAX);
                        sim.generate("OPT");
                        sim.print();
                    }
                    break;
                case "6": //LRU
                    if (refStringFlag) {
                        sim = new MemorySim(referenceString, numOfPhysicalFrames, VPAGEMAX);
                        sim.generate("LRU");
                        sim.print();
                    }
                    break;
                case "7": //LFU
                    if (refStringFlag) {
                        sim = new MemorySim(referenceString, numOfPhysicalFrames, VPAGEMAX);
                        sim.generate("LFU");
                        sim.print();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //Reads string from user input. Is passed the class scanner as a parameter.
    private static ArrayList<Integer> readRefString(Scanner scanner){
        ArrayList<Integer> list = new ArrayList<>();

        System.out.println("Enter your reference string, separating each number with a space: ");

        //Runs at least once
        do{
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            String temp;
            int tempInt = -1;
            boolean isInt;

            //Scans each "token", ensuring it is a valid integer
            while(lineScanner.hasNext()){
                temp = lineScanner.next();
                isInt = false;
                try{
                    tempInt = Integer.parseInt(temp);
                    isInt = true;
                } catch (NumberFormatException e){
                    System.out.println("Non-integer entered: " + temp);
                }

                if (isInt && (tempInt < 0 || tempInt >= VPAGEMAX)){
                    System.out.println("Number must be between 0 and 9. " + temp + " ignored.");
                } else if (isInt){
                    list.add(tempInt);
                }
            }
            if(list.size() < 1){
                System.out.println("Please enter at least one valid number.");
            }
        } while (list.size() < 1);

        //Returns an ArrayList to be parsed as the reference string.
        return list;
    }

    //Randomly generates a reference string to a size determined by the user
    private static ArrayList<Integer> generateString(Scanner scanner){
        int size;
        System.out.println("How long will the reference string be?");
        size = Integer.parseInt(scanner.next());

        if(size < 1){
            System.out.println("Length must be at least one.");
            return null;
        }

        Random random = new Random();

        ArrayList<Integer> list = new ArrayList<>();

        for(int i = 0; i < size; i++){
            list.add(random.nextInt(VPAGEMAX));
        }
        //Returns an ArrayList to be parsed as the reference string.
        return list;
    }

    //Ensures a string is loaded, then prints it
    static void validateReferenceString(ArrayList<Integer> referenceString){
        if(referenceString != null){
            System.out.println("Reference String: ");
            int i;
            for(i = 0; i < referenceString.size() - 1; i++){
                System.out.print(referenceString.get(i) + ", ");
            }
            System.out.println(referenceString.get(i));
            refStringFlag = true;
        } else {
            System.out.println("Please load a valid reference string.");
        }
    }
}
