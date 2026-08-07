package com.taskmanager.domain.assistant;

public interface AnswerFormatter {
    String format(String instructions, String data);
}