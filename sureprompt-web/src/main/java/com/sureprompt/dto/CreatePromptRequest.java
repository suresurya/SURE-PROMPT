package com.sureprompt.dto;

import com.sureprompt.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreatePromptRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Prompt body is required")
    private String promptBody;

    @NotBlank(message = "AI output is required")
    private String aiOutput;

    @NotNull(message = "At least one tag is required")
    @Size(min = 1, max = 5, message = "Please select between 1 and 5 tags")
    private List<String> tags;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotBlank(message = "Platform is required")
    private String platform;
}
