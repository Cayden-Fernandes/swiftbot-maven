package simon.swift01;

import swiftbot.*;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Test02 {
    static SwiftBotAPI swiftBot;
    static ArrayList<String> gameSequence;
    static ArrayList<String> playerSequence;
    static boolean playerFailed;
    static int score;
    static boolean gameRunning;
    
    // --- CALIBRATION SETTINGS ---
    // You must adjust this! How many milliseconds to move 1cm at speed 100?
    // Start with 50, then increase/decrease based on testing.
    static final int CALIBRATION_FACTOR = 50; 

    // --- MAPPING ---
    // Maps buttons to their specific Light locations
    static final Underlight[] LIGHT_MAP = {
        Underlight.FRONT_LEFT,  // Mapped to Button A
        Underlight.FRONT_RIGHT, // Mapped to Button B
        Underlight.BACK_LEFT,   // Mapped to Button X
        Underlight.BACK_RIGHT   // Mapped to Button Y
    };
    
    // Maps buttons to human-readable colors for the console
    static final String[] COLOR_NAMES = {"RED", "GREEN", "BLUE", "YELLOW"};
    
    // Standard colors for the lights
    static final int[] RED = {255, 0, 0};
    static final int[] GREEN = {0, 255, 0};
    static final int[] BLUE = {0, 0, 255};
    static final int[] YELLOW = {255, 255, 0};
    static final int[][] COLORS = {RED, GREEN, BLUE, YELLOW};

    public static void main(String[] args) {
        // 1. Initialization
        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("Error: I2C disabled. Please enable it on the Pi.");
            System.exit(5);
        }

        System.out.println("Welcome to Simon Swift!");
        Scanner scanner = new Scanner(System.in);
        
        // 2. Main Game Loop
        gameRunning = true;
        score = 0;
        gameSequence = new ArrayList<>();
        Random rand = new Random();

        while (gameRunning) {
            System.out.println("\n--- ROUND " + (score + 1) + " ---");
            System.out.println("Current Score: " + score);
            
            // Add a new random color (0=A, 1=B, 2=X, 3=Y)
            int nextColorIndex = rand.nextInt(4);
            // We store the index as a String "0", "1", etc. for easy comparison
            gameSequence.add(String.valueOf(nextColorIndex));
            
            // 3. Display Sequence to User
            System.out.println("Watch the lights...");
            playSequence(gameSequence);

            // 4. Get User Input
            System.out.println("Your turn! Repeat the sequence using buttons A, B, X, Y.");
            boolean roundSuccess = getUserInput(gameSequence.size());

            if (roundSuccess) {
                System.out.println("Correct!");
                score++;
                
                // Check if user wants to quit (Every 5th round)
                if (score % 5 == 0) {
                    System.out.print("Great job! You reached round " + score + ". Do you want to continue? (Y/N): ");
                    String choice = scanner.next();
                    if (choice.equalsIgnoreCase("N")) {
                        System.out.println("See you again champ!");
                        performCelebration(score); // Optional: Celebration on quit if high score?
                        gameRunning = false;
                    }
                }
            } else {
                System.out.println("Wrong! Game Over!");
                System.out.println("Final Score: " + score);
                System.out.println("Round Reached: " + (score + 1));
                
                // 5. Celebration Condition
                if (score >= 5) {
                    performCelebration(score);
                }
                
                gameRunning = false;
            }
            
            // Small pause between rounds
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
        
        scanner.close();
        System.exit(0);
    }

    /**
     * Plays the stored sequence of lights
     */
    public static void playSequence(ArrayList<String> sequence) {
        try {
            for (String s : sequence) {
                int index = Integer.parseInt(s);
                // Turn on specific light
                swiftBot.setUnderlight(LIGHT_MAP[index], COLORS[index]);
                Thread.sleep(800); // Light on for 0.8 seconds
                
                // Turn off
                swiftBot.disableUnderlight(LIGHT_MAP[index]);
                Thread.sleep(400); // Pause between lights
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * waits for the player to press buttons.
     * Returns TRUE if they get the sequence right, FALSE otherwise.
     */
    public static boolean getUserInput(int sequenceLength) {
        playerSequence = new ArrayList<>();
        playerFailed = false;

        // Enable all buttons
        // Note: Using lambda '() -> handleButton(index)' to simplify logic
        swiftBot.enableButton(Button.A, () -> handleButtonPress(0));
        swiftBot.enableButton(Button.B, () -> handleButtonPress(1));
        swiftBot.enableButton(Button.X, () -> handleButtonPress(2));
        swiftBot.enableButton(Button.Y, () -> handleButtonPress(3));

        // Wait loop: wait until player enters full sequence OR makes a mistake
        while (playerSequence.size() < sequenceLength && !playerFailed) {
            try {
                Thread.sleep(100); // Small delay to prevent CPU hogging
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Disable buttons so accidental presses don't count later
        swiftBot.disableAllButtons();

        return !playerFailed;
    }

    /**
     * Callback for when a button is pressed.
     * checks the input immediately against the game sequence.
     */
    public static void handleButtonPress(int buttonIndex) {
        System.out.println("Button " + getButtonName(buttonIndex) + " pressed.");
        
        // Flash the light for feedback
        swiftBot.setUnderlight(LIGHT_MAP[buttonIndex], COLORS[buttonIndex]);
        try { Thread.sleep(200); } catch (Exception e) {}
        swiftBot.disableUnderlight(LIGHT_MAP[buttonIndex]);

        // Add to player's current attempt
        playerSequence.add(String.valueOf(buttonIndex));

        // Check if this latest press matches the game sequence at the same position
        int currentStep = playerSequence.size() - 1;
        String expected = gameSequence.get(currentStep);
        String actual = String.valueOf(buttonIndex);

        if (!expected.equals(actual)) {
            playerFailed = true; // Flag to exit the wait loop
        }
    }

    /**
     * The Celebration Dive (V-Shape) logic.
     */
    public static void performCelebration(int finalScore) {
        System.out.println("Initiating Celebration Dive!");

        // Calculate Speed
        int speed = finalScore * 10;
        if (finalScore < 5) speed = 40;     // Should not happen if logic is correct, but safe fallback
        if (finalScore >= 10) speed = 100;  // Cap at 100

        // Calculate Time for 30cm
        // Formula: Time (ms) = (Distance * Calibration) / Speed
        // Note: This is an approximation. You must calibrate 'CALIBRATION_FACTOR' at top of file.
        int moveTime = (30 * CALIBRATION_FACTOR * 100) / speed; 

        try {
            // Flash all lights random colors
            swiftBot.fillUnderlights(RED);
            Thread.sleep(200);
            swiftBot.fillUnderlights(GREEN);
            Thread.sleep(200);
            
            // 1. Turn Left to start V shape
            // (Left wheel reverse, Right wheel forward) creates a spin
            swiftBot.move(-50, 50, 500); 
            
            // 2. Move Forward (First Arm of V)
            swiftBot.move(speed, speed, moveTime);
            
            // 3. Move Backward (Return to center)
            swiftBot.move(-speed, -speed, moveTime);
            
            // 4. Turn Right to align for second arm
            // Spin longer to face the other way
            swiftBot.move(50, -50, 1000); 
            
            // 5. Move Forward (Second Arm of V)
            swiftBot.move(speed, speed, moveTime);
            
            // 6. Move Backward (Return to center)
            swiftBot.move(-speed, -speed, moveTime);
            
            // 7. Re-center
            swiftBot.move(-50, 50, 500);

            // Final Flash
            swiftBot.fillUnderlights(YELLOW);
            Thread.sleep(1000);
            swiftBot.disableUnderlights();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Helper to get button names for console
    public static String getButtonName(int index) {
        switch(index) {
            case 0: return "A";
            case 1: return "B";
            case 2: return "X";
            case 3: return "Y";
            default: return "?";
        }
    }
}