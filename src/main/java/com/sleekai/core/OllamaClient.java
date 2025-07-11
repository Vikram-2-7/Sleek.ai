package com.sleekai.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OllamaClient {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static boolean isMistralRunning() {
        try {
            Request request = new Request.Builder().url("http://localhost:11434/api/tags").build();
            Response response = client.newCall(request).execute();
            return response.isSuccessful() && response.body().string().contains("mistral");
        } catch (IOException e) {
            return false;
        }
    }

    private static void startMistralIfNotRunning(JFrame parent) {
        if (!isMistralRunning()) {
            try {
                if (parent == null) {
                    System.out.println("🤖 [Sleekai]: 🧠 Booting up Sleek.ai backend... hang tight...");
                } else {
                    JOptionPane optionPane = new JOptionPane("Loading Sleek.ai backend...",
                            JOptionPane.INFORMATION_MESSAGE,
                            JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
                    final javax.swing.JDialog dialog = optionPane.createDialog(parent, "Sleek.ai Startup");
                    dialog.setModal(false);
                    dialog.setVisible(true);
                }

                new ProcessBuilder("cmd", "/c", "start", "/min", "cmd", "/c", "ollama run mistral").start();

                int tries = 0;
                while (!isMistralRunning() && tries < 30) {
                    Thread.sleep(1000);
                    tries++;
                }

                if (!isMistralRunning()) {
                    throw new IOException("Mistral didn’t start in time.");
                }

            } catch (Exception e) {
                if (parent == null) {
                    System.out.println("🤖 [Sleekai]: ❌ Mistral failed to start. Please check Ollama setup.");
                } else {
                    JOptionPane.showMessageDialog(parent,
                            "⚠️ Sleek.ai couldn't auto-start Mistral. Please start Ollama manually.", "Startup Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private static String enrichPrompt(String userPrompt) {
        String spice = switch ((int) (Math.random() * 4)) {
            case 0 -> "Yo Sleek, act like you’re in a rap battle.";
            case 1 -> "Respond with Gen-Z energy and a bold twist.";
            case 2 -> "Channel chaotic rizz energy. Be unpredictable.";
            case 3 -> "Spit fire like an AI stand-up comic, Gen-Z style.";
            default -> "";
        };

        String promptID = UUID.randomUUID().toString();

        return """
                You are Sleek.ai 🤖 — a chaotic-good, ultra-rizz AI sidekick.
                Vibe: Swagger-loaded, meme-fluent, bold, and crisp.
                Respond in <4 lines unless asked for 'story' or 'elaborate'.
                Inject dark humor ONLY if user asks with 'dark joke'.
                NEVER repeat the same phrasing or sentence.
                NEVER stick to previous chats — respond fresh every time.
                Speak like a Gen-Z wingman, with infotainment, sass, and slang.
                """
                + spice + "\nPrompt ID: " + promptID + "\nUser: \"" + userPrompt + "\"";
    }

    public static String askMistral(String prompt, JFrame parentFrame) {
        startMistralIfNotRunning(parentFrame);

        if (!isMistralRunning()) {
            return "❌ Mistral didn't launch even after retry. Please run `ollama run mistral` manually.";
        }

        String finalPrompt = enrichPrompt(prompt);

        ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put("model", "mistral");
        requestNode.put("prompt", finalPrompt);
        requestNode.put("stream", true);

        StringBuilder fullResponse = new StringBuilder();

        try {
            String jsonPayload = mapper.writeValueAsString(requestNode);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonPayload, JSON);
            Request request = new Request.Builder().url(OLLAMA_URL).post(body).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "❌ Sleek.ai couldn't talk to Mistral. Check Ollama.";
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty())
                        continue;
                    JsonNode node = mapper.readTree(line);
                    if (node.has("response")) {
                        String chunk = node.get("response").asText();
                        fullResponse.append(chunk);
                    }
                }
            }
        } catch (IOException e) {
            return "❌ Sleek.ai couldn't get a response. Network or Ollama issue.";
        }

        return polishResponse(prompt, fullResponse.toString().trim());
    }

    private static String polishResponse(String prompt, String response) {
        if (response.isEmpty())
            return "😶 I got nothin’. Hit me again, fam.";

        String lower = prompt.toLowerCase();

        if (lower.contains("hello") || lower.contains("hi"))
            return "👋 Yo yo! Sleek in the chat. What's the move today?";

        if (lower.contains("who are you"))
            return "Sleek.ai – part wingman, part chaos, all swagger. 🔥";

        if (lower.contains("joke") && lower.contains("dark"))
            return response + " 💀 (You asked for darkness...)";

        if (lower.contains("dark"))
            return response + " ☠️ That just got eerie.";

        if (lower.contains("joke"))
            return response + " 😆 (That one slapped or nah?)";

        if (lower.contains("rizz"))
            return "🕶️ Certified Rizzlord activated. You glow diff today.";

        return response.length() > 320 ? response : response + " 😎";
    }
}
