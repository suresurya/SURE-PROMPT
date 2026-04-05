package com.suresurya.sureprompt.models;

public class RunRequest {
    private String prompt;

    public RunRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
