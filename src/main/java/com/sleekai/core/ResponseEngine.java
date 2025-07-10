package com.sleekai.core;

import java.util.*;

public class ResponseEngine {
    private Random random = new Random();

    public String getResponse(String mood, List<String> tokens) {
        if (tokens.contains("hello")) {
            return "Hey there! I’m sleek.ai 🤖—your creative buddy. What’s up?";
        }
        if (tokens.contains("joke")) {
            return getRandomJoke();
        }
        if (tokens.contains("weather")) {
            return "I don't have live weather data yet, but it feels sleek to me!";
        }
        if (tokens.contains("learn") || tokens.contains("discover")) {
            return "I love learning! Tell me something new, and I'll remember it.";
        }
        if (mood.equals("sad")) {
            return "Oh no 😢. I’m here for you. Sending you good vibes and a virtual hug.";
        }
        if (mood.equals("happy")) {
            return "Yay! I’m glad you’re feeling happy 🥳.";
        }
        if (mood.equals("angry")) {
            return "Take a deep breath. Remember, you’re awesome 💪.";
        }
        if (mood.equals("bored")) {
            return "Bored? Let’s play a game! Or ask me for a fun fact.";
        }
        return getDefaultResponse();
    }

    private String getRandomJoke() {
        String[] jokes = {
            "Why did the developer go broke? Because he used up all his cache!",
            "I would tell you a joke about UDP, but you might not get it.",
            "01001100 01101111 01101100—that’s ‘LOL’ in binary!"
        };
        return jokes[random.nextInt(jokes.length)];
    }

    private String getDefaultResponse() {
        String[] responses = {
            "Hmm, that’s interesting! Tell me more.",
            "I’m still learning about that. Want to teach me?",
            "Let’s explore that topic together!",
            "That’s a great question. I’ll get better at answering it soon!"
        };
        return responses[random.nextInt(responses.length)];
    }
}
