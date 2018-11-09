package edu.gatech.Marta;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Mass Transit Simulation System Starting...");
        SimDriver commandInterpreter = new SimDriver();

        final String DELIMITER = ",";
        Scanner takeCommand = new Scanner(System.in);
        String[] tokens;

        do {
            String userCommandLine = takeCommand.nextLine();
            tokens = userCommandLine.split(DELIMITER);
            commandInterpreter.runInterpreter(userCommandLine);
        } while (!tokens[0].equals("quit"));
        takeCommand.close();
    }
}
