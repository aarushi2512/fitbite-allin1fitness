package com.example.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSplitter {

    public static void main(String inputtext) {


        // Define patterns for the three parts
        Pattern textPattern = Pattern.compile("([a-zA-Z ]+)");

        Pattern quantityPattern = Pattern.compile("\\((\\d+(\\.\\d+)?)\\)");
        Pattern unitPattern = Pattern.compile("\\(([a-zA-Z ]+)\\)");

        // Create matchers for each pattern
        Matcher textMatcher;
        textMatcher = textPattern.matcher(inputtext);
        Matcher quantityMatcher = quantityPattern.matcher(inputtext);
        Matcher unitMatcher = unitPattern.matcher(inputtext);
        // Extract and print the three parts
        if (textMatcher.find()) {
            String text = textMatcher.group(1);
            System.out.println("1. Text: " + text);
        }

        if (quantityMatcher.find()) {
            String quantity = quantityMatcher.group(1);
            System.out.println("2. Quantity: " + quantity);
        }

        if (unitMatcher.find()) {
            String unit = unitMatcher.group(1);
            System.out.println("3. Unit: " + unit);
        }
    }
}
