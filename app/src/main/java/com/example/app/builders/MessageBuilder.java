package com.example.app.builders;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageBuilder {
    private List<Integer> numbers;

    public MessageBuilder() {
        this.numbers = new ArrayList<>();
    }

    private boolean isValidNumber(String number) {
        try {
            int num = Integer.parseInt(number);
            return num >= 1 && num <= 64;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void add(String number) {
        if (isValidNumber(number)) {
            int parsedNumber = Integer.parseInt(number);
            if (numbers.contains(parsedNumber)) {
                numbers.remove(Integer.valueOf(parsedNumber));
            } else {
                numbers.add(parsedNumber);
            }
        } else {
            Log.d("MessageBuilder", "Invalid number: " + number);
        }
    }

    public void remove(String number) {
        int num = Integer.parseInt(number);
        numbers.remove(Integer.valueOf(num));
    }

    public void printMessage() {
        Collections.sort(numbers);
        StringBuilder sb = new StringBuilder();
        for (Integer number : numbers) {
            sb.append(String.format("%02d", number));
        }
        Log.d("MessageBuilder", sb.toString());
    }

    public String getMessage(){
        Collections.sort(numbers);
        StringBuilder sb = new StringBuilder();
        for (Integer number : numbers) {
            sb.append(String.format("%02d", number));
        }

        return sb.toString();
    }
}
