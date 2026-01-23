package simon.swift01;

import swiftbot.*;
import java.io.IOException;
import java.util.Scanner;

public class RemoteControl {

    static SwiftBotAPI swiftBot;
    static final int SPEED = 100;
    static final int TURN_SPEED = 100;
    static char currentKey = ' '; // Track current key being pressed
    static boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("****************Hello THis Is Simon Swift***********************\n" + "\n");

        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("\nI2C disabled!");
            System.exit(5);
        }

        Scanner reader = new Scanner(System.in);

        while (true) {

            System.out.println(
                            "Enter 1 to custom move like a remote control car\n" +
                            "Enter 2 to blink lights\n" +    
                            "Enter 0 to Exit\n");

            String input = reader.next();

            switch (input) {
                
                case "1":
                    remoteControlMode();
                    break;

                case "2":
                    blinkLights();
                    break;

                case "0":
                    System.out.println("Exiting... Goodbye!");
                    reader.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid input. Please try again.\n");
                    break;
            }
        }
    }

    // Blink the underlights
    public static void blinkLights() throws InterruptedException {
        System.out.println("Blinking lights...\n");
        int[] red = {255, 0, 0};
        int[] green = {0, 255, 0};
        int[] blue = {0, 0, 255};

        for (int i = 0; i < 5; i++) {
            swiftBot.fillUnderlights(red);
            Thread.sleep(1000);
            swiftBot.fillUnderlights(green);
            Thread.sleep(200);
            swiftBot.fillUnderlights(blue);
            Thread.sleep(200);
        }
        swiftBot.disableUnderlights();
        System.out.println("Lights off.\n");
    }

    // Remote control car mode using keyboard
    public static void remoteControlMode() {
        System.out.println("\n********** REMOTE CONTROL MODE ************");
        System.out.println("Controls:");
        System.out.println("  W - Move Forward");
        System.out.println("  S - Move Backward");
        System.out.println("  A - Turn Left");
        System.out.println("  D - Turn Right");
        System.out.println("  X - Stop");
        System.out.println("  Q - Exit Remote Control Mode\n");

        try {
            // Enable raw mode for character by character reading
            setRawMode(true);
            running = true;
            currentKey = 'x'; // Start stopped

            System.out.println("Remote control active! Use WASD keys to move.\n");

            // Create a thread for continuous movement
            Thread movementThread = new Thread(() -> {
                try {
                    while (running) {
                        executeMovement(currentKey);
                        Thread.sleep(50); // Update movement every 50ms
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            movementThread.start();

            // Main input loop
            while (running) {
                if (System.in.available() > 0) {
                    int input = System.in.read();
                    char key = Character.toLowerCase((char) input);
                    
                    // Update current key
                    currentKey = key;

                    // Exit on 'q'
                    if (key == 'q') {
                        running = false;
                        break;
                    }
                }

                Thread.sleep(10); // Small delay
            }

            // Wait for movement thread to finish
            movementThread.join();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cleanup
            try {
                stopRobot();
                setRawMode(false);
                System.out.println("\nExiting remote control mode...\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    //Execute movement based on current key
    public static void executeMovement(char key) throws InterruptedException {
        switch (key) {
            case 'w': // Forward
                System.out.print(" Moving Forward   \r");
                swiftBot.move(SPEED, SPEED, 50);
                break;

            case 's': // Backward
                System.out.print(" Moving Backward  \r");
                swiftBot.move(-SPEED, -SPEED, 50);
                break;

            case 'a': // Turn Left
                System.out.print(" Turning Left     \r");
                swiftBot.move(-TURN_SPEED, TURN_SPEED, 50);
                break;

            case 'd': // Turn Right
                System.out.print(" Turning Right    \r");
                swiftBot.move(TURN_SPEED, -TURN_SPEED, 50);
                break;

            case 'x': // Stop
            case ' ': // Space also stops
                System.out.print(" Stopped          \r");
                stopRobot();
                break;

            default:
                // For any other key, stop
                stopRobot();
                break;
        }
    }

    //Stop the robot
    public static void stopRobot() throws InterruptedException {
        swiftBot.move(0, 0, 100);
    }

    
    //Enable/disable raw terminal mode for immediate key reading
    public static void setRawMode(boolean enable) throws IOException, InterruptedException {
        if (enable) {
            // Disable canonical mode and echo
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "stty raw -echo < /dev/tty"}).waitFor();
        } else {
            // Re-enable canonical mode and echo
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "stty sane < /dev/tty"}).waitFor();
        }
    }
}