package simon.swift01;
//import swiftbot.SwiftBotAPI;

import java.util.Scanner;

import swiftbot.*;


public class SwiftbotSimon {
    public static void main(String[] args) {
        
        Scanner reader = new Scanner(System.in);
        
        while (true){
                System.out.printf("This Is me Simon Swift\n" + 
                
                "Enter 1 to move forward\n" +
                "Enter 2 to move backward\n" +
                "Enter 3 to take Right Turn\n" +
                "Enter 4 to take Left Turn\n" +
                "Enter 0 to Exit\n"               
                
                );

                String input =reader.next();
        
        switch(input) {

            case"1":
            SwiftBotAPI.INSTANCE.move(100,100,7000);
            break;

            case "2":
            SwiftBotAPI.INSTANCE.move(-100,-100, 1000);
            break;

            case "0":
            System.out.println("----Exit-----");
            reader.close();
            break;

            default:
            System.out.println("***************Enter a valid input***************");
            break;


            }   

        }
        
    }
}