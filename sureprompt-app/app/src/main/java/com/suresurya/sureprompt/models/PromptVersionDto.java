package com.suresurya.sureprompt.models;

public class PromptVersionDto {
    private Long id;
    private Integer version;
    private String promptText;
    private String aiOutput;

    // Getters and Setters
    public Long getId() { return id; }
    public Integer getVersion() { return version; }
    public String getPromptText() { return promptText; }
    public String getAiOutput() { return aiOutput; }
}
