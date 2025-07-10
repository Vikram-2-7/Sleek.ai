package com.sleekai.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NLPProcessor {
    private StanfordCoreNLP pipeline;
    private Set<String> dictionary;

    public NLPProcessor() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
        props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser");


        // No custom model paths – let CoreNLP use default models
        pipeline = new StanfordCoreNLP(props);

        dictionary = new HashSet<>(Arrays.asList(
                "hello", "weather", "happy", "sad", "news", "joke", "help",
                "location", "time", "date", "fact", "learn", "discover", "fun",
                "love", "angry", "bored", "silly"
        ));
    }

    public List<String> tokenize(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<String> tokens = new ArrayList<>();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                tokens.add(token.lemma().toLowerCase());
            }
        }
        return tokens;
    }

    public String correctSpelling(String word) {
        for (String dictWord : dictionary) {
            int distance = StringUtils.getLevenshteinDistance(word, dictWord);
            if (distance <= 2) {
                return dictWord;
            }
        }
        return word;
    }

    public boolean mentionsPlanet(List<String> tokens) {
        return tokens.contains("planet");
    }

    public String detectMood(List<String> tokens) {
        if (tokens.contains("sad")) return "sad";
        if (tokens.contains("happy")) return "happy";
        if (tokens.contains("angry")) return "angry";
        if (tokens.contains("bored")) return "bored";
        return "neutral";
    }

    public String extractPersonName(String input) {
        String lower = input.toLowerCase().trim();
        String name = null;

        if (lower.startsWith("i am ")) {
            name = input.substring(5).trim();
        } else if (lower.startsWith("iam ")) {
            name = input.substring(4).trim();
        } else if (lower.startsWith("i'm ")) {
            name = input.substring(4).trim();
        } else if (lower.startsWith("im ")) {
            name = input.substring(3).trim();
        } else if (lower.startsWith("my name is ")) {
            name = input.substring(11).trim();
        }

        if (name != null && !name.isEmpty()) {
            return StringUtils.capitalize(name);
        }
        return null;
    }

    public List<String> extractLemmas(String input) {
        Annotation annotation = new Annotation(input);
        pipeline.annotate(annotation);
        List<String> lemmas = new ArrayList<>();

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.lemma().toLowerCase());
            }
        }
        return lemmas;
    }
}
