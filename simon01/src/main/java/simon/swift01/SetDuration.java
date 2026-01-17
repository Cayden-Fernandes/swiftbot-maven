package simon.swift01;

import swiftbot.SwiftBotAPI;
import java.util.Scanner;

public class SetDuration {
    
    static SwiftBotAPI swiftbot = SwiftBotAPI.INSTANCE;
    static boolean isMoving = false;
    static int speed;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        
        System.out.println("SwiftBot Distance Calibration");
        
        // Start single movement control thread that runs entire program
        Thread movementThread = new Thread(() -> {
            try {
                while (true) {
                    if (isMoving) {
                        swiftbot.move(speed, speed, 100);
                    }
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                // Thread interrupted, exit
            }
        });
        movementThread.setDaemon(true); // Dies when main program ends
        movementThread.start();
        
        while (!quit) {
            // Ask user to set movement speed
            speed = askForSpeed(scanner);
            boolean stayOnThisSpeed = true;
            
            while (stayOnThisSpeed && !quit) {
                System.out.println("\nPress:");
                System.out.println("  W - Move SwiftBot forward");
                System.out.println("  X - Stop SwiftBot");
                System.out.println("  Q - Quit program");
                System.out.print("Your choice: ");
                
                String choice = scanner.nextLine().trim().toUpperCase();
                
                if (choice.equals("W")) {
                    System.out.println("Moving forward at " + speed + "% ...");
                    isMoving = true;
                    
                } else if (choice.equals("X")) {
                    System.out.println("Stopping SwiftBot...");
                    isMoving = false;
                    swiftbot.stopMove();
                    
                    // Ask if user wants to change speed
                    System.out.print("Do you want to change the speed? (Y/N): ");
                    String change = scanner.nextLine().trim().toUpperCase();
                    if (change.equals("Y")) {
                        stayOnThisSpeed = false; // Exit to change speed
                    } else {
                        System.out.println("Keeping current speed: " + speed + "%");
                    }
                    
                } else if (choice.equals("Q")) {
                    System.out.println("Quitting program...");
                    isMoving = false;
                    swiftbot.stopMove();
                    quit = true;
                    
                } else {
                    System.out.println("Invalid input. Please press W, X or Q.");
                }
            }
        }
        
        scanner.close();
        System.out.println("Program ended.");
    }
    
    // Helper method to get a valid speed from 10 to 100
    private static int askForSpeed(Scanner scanner) {
        int speed = 0;
        while (true) {
            System.out.print("\nSet movement speed (10 to 100): ");
            String input = scanner.nextLine().trim();
            try {
                speed = Integer.parseInt(input);
                if (speed >= 10 && speed <= 100) {
                    System.out.println("Speed set to " + speed + "%");
                    return speed;
                } else {
                    System.out.println("Please enter a value between 10 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}