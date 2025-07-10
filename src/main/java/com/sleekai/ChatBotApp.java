package com.sleekai;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.sleekai.core.OllamaClient;

public class ChatBotApp {
    public static void main(String[] args) {
        // Enable UTF-8 output for emoji support
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("sleek.ai: 🌟 Hello! Ask me anything. Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("sleek.ai: 👋 Bye-bye, sleek human. Until next time!");
                break;
            }

            String response = OllamaClient.askMistral(userInput);
            System.out.println("sleek.ai: " + response);
        }

        scanner.close();
    }
}
